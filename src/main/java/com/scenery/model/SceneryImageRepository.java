package com.scenery.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List; // 如果您希望返回多個圖片

public interface SceneryImageRepository extends JpaRepository<SceneryImageVO, Integer> {

    // 原始方法 (錯誤的)
    // Optional<SceneryImageVO> findBySceneryId(Integer sceneryId);

    // 正確的方法：遍歷 SceneryImageVO.scenery 物件，然後找 SceneryVO 的 sceneryId
    Optional<List<SceneryImageVO>> findByScenery_SceneryId(Integer sceneryId);
    // 或者如果您確定每個 sceneryId 只對應一張圖片（這不太可能對於圖片庫），則可以是 Optional<SceneryImageVO>
    // 但通常一個景點有多張圖片，所以用 List 比較合理

    // 如果您需要找某個圖片ID
    // Optional<SceneryImageVO> findBySceneryImageId(Integer sceneryImageId);

}