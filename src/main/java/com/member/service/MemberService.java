package com.member.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.member.model.FavoriteSceneryRepository;
import com.member.model.FavoriteSceneryVO;
import com.member.model.FavoriteTravelPlan;
import com.member.model.FavoriteTravelPlanRepository;
import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.scenery.model.SceneryVO;
import com.travel_plan.model.TravelPlan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class MemberService {

//    @Autowired
    private MemberRepository memberRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final MailService mailService;
    
    private final PasswordEncoder passwordEncoder;
    
    private final RedisTemplate<String, String> redisTemplate;

    private FavoriteSceneryRepository favoriteSceneryRepo;

    public MemberService(MemberRepository memberRepository, 
    					MailService mailService,
    					RedisTemplate<String, String> redisTemplate,
    					FavoriteSceneryRepository favoriteSceneryRepo) {
        this.memberRepository = memberRepository;
        this.mailService = mailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.redisTemplate = redisTemplate;
        this.favoriteSceneryRepo = favoriteSceneryRepo;
    }

    //會員註冊(新增)動作
    public memVO register(memVO member) {
        // 可加上帳號是否存在判斷、密碼加密等
    	if(memberRepository.findByMemberAccount(member.getMemberAccount()).isPresent()) {
    		throw new RuntimeException("帳號已經存在喔！");
    	}
        return memberRepository.save(member);
    }
    
    //查詢所有會員
    public List<memVO> findAllMembers() {
        return memberRepository.findAll();
    }

    //使用id來查詢會員
    public Optional<memVO> findById(Integer id){
    	return memberRepository.findById(id);
    }

    //用帳號來查詢會員(帳號登入用)
    public Optional<memVO> findByAccount(String memberAccount) {
        return memberRepository.findByMemberAccount(memberAccount);
    }

    
    //++新增 或 修改會員 (ID有值就更新，沒值就新增)
    public memVO save(memVO member) {
        memVO saved = memberRepository.save(member);
        entityManager.flush(); // ✅ 這行會真正寫進資料庫
        return saved;
    }
    
    //XX刪除會員
    public void deleteById(Integer id) {
    	memberRepository.deleteById(id);
    }
    
    
    public Optional<memVO> findByToken(String token){
        System.out.println("收到驗證 token: " + token);
        
        Optional<memVO> optionalMember = memberRepository.findByVerifyToken(token);
        System.out.println("是否查到會員: " + optionalMember.isPresent());
        
        return optionalMember;
    }
    
    //註冊後產生驗證信連結
    public void sendVerificationalEmail (memVO member) {
    	String token = UUID.randomUUID().toString();
        // ✅ 把驗證碼存進 Redis，而不是存到資料表
        saveVerifyTokenToRedis(member.getMemberAccount(), token);

        // ✅ 把 account 一起放進網址參數，讓 controller 能比對
        String verifyUrl = "http://localhost:8080/member/verify?account=" 
                           + member.getMemberAccount() + "&token=" + token;
        
        System.out.println("[DEBUG] Redis 寫入：verify:" + member.getMemberAccount() + " → " + token);

        mailService.sendVerificationEmail(member, verifyUrl);
    }
    
    //點擊信箱連結驗證
//    public boolean verifyEmail(String token) {
//    	Optional<memVO> optional = memberRepository.findByVerifyToken(token);
//    	if(optional.isPresent()) {
//    		memVO member = optional.get();
//    		member.setEmailVerified(true);
//    		member.setVerifyToken(null);
//    		memberRepository.save(member);
//    		return true;
//    	}
//    	return false;
//    }
    
    //忘記密碼 - 流程
    public void generateResetToken(memVO member) {
    	String token = UUID.randomUUID().toString();
    	member.setResetToken(token);
    	member.setResetTokenCreatedTime(LocalDateTime.now());
    	memberRepository.save(member);
    	
    	String resetUrl = "http://localhost:8080/member/reset-password?token=" + token;
    	mailService.sendResetPasswordEmail(member.getMemberEmail(), resetUrl);
    }
    
    public Optional<memVO> findByResetToken(String token){
    	return memberRepository.findByResetToken(token);
    }
    
    public void resetPassword(memVO member, String newPassword) {
//    	member.setMemberPassword(passwordEncoder.encode(newPassword)); //上限記得要更換
    	member.setMemberPassword(newPassword); //明碼測試 上限要刪掉
    	member.setResetToken(null);
    	member.setResetTokenCreatedTime(null);
    	memberRepository.save(member);
    }
    
    
    
    
 // 1. 忘記密碼流程：寄送 reset link
    public void processForgotPassword(String email) {
        Optional<memVO> optional = memberRepository.findByMemberEmail(email);
        if (optional.isEmpty()) {
            throw new RuntimeException("找不到此 Email！");
        }

        memVO member = optional.get();
        String token = UUID.randomUUID().toString();
        member.setResetToken(token);
        member.setResetTokenCreatedTime(LocalDateTime.now());
        memberRepository.save(member);

        String resetLink = "http://localhost:8080/member/reset?token=" + token;
        mailService.sendResetPasswordEmail(email, resetLink);
    }

    // 2. 驗證 resetToken 是否有效（30 分鐘內）
    public boolean isResetTokenValid(String token) {
        Optional<memVO> optional = memberRepository.findByResetToken(token);
        if (optional.isEmpty()) return false;

        memVO member = optional.get();
        return member.getResetTokenCreatedTime().isAfter(LocalDateTime.now().minusMinutes(30));
    }

    // 3. 執行密碼重設
    public void resetPassword(String token, String newPassword) {
        Optional<memVO> optional = memberRepository.findByResetToken(token);
        if (optional.isEmpty()) {
            throw new RuntimeException("Token 無效或已使用");
        }

        memVO member = optional.get();
        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword); // 密碼加密
        member.setMemberPassword(encodedPassword);
        member.setResetToken(null);
        member.setResetTokenCreatedTime(null);
        memberRepository.save(member);
    }
    
    //==========Redis========================
    // 👉 這個方法也放在 MemberService 裡
    public void saveVerifyTokenToRedis(String account, String token) {
        String key = "verify:" + account;
        redisTemplate.opsForValue().set(key, token, 10, TimeUnit.MINUTES);
    }
    
    public boolean checkVerifyTokenFromRedis(String account, String inputToken) {
    	String key = "verify:" + account;
    	String tokenInRedis = redisTemplate.opsForValue().get(key);
    	
    	if(tokenInRedis != null && tokenInRedis.equals(inputToken)) {
    		//成功驗證後，刪除Redis 裡的驗證碼
    		redisTemplate.delete(key);
    		return true;
    	}else {
    		return false;
    	}
    }
    
}

