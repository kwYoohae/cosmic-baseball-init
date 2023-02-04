package com.hyunec.cosmicbaseballinit.domain.baseball.model.base;

import com.hyunec.cosmicbaseballinit.domain.baseball.model.hit.HitType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Base {

  private final List<Boolean> base = new ArrayList<>(BaseType.getBaseTypeSize());
  private static final Boolean existPlate = true;

  public Boolean checkPlateExist(BaseType baseType) {
    return base.get(baseType.getBasePosition());
  }

  public int hit(HitType hitType) {
    if (hitType == HitType.SINGLE_HIT) {
      return singleHit();
    }
    return 0;
  }

  private int singleHit() {
    int score = 0;
    if (!base.get(BaseType.FIRST_BASE.getBasePosition())) {
      base.set(BaseType.FIRST_BASE.getBasePosition(), existPlate);
      return score;
    }

    if (base.get(BaseType.SECOND_BASE.getBasePosition())) {
      if (base.get(BaseType.THIRD_BASE.getBasePosition())) {
        score++;
        return score;
      }
      base.set(BaseType.THIRD_BASE.getBasePosition(), existPlate);
      return score;
    }
    base.set(BaseType.SECOND_BASE.getBasePosition(), existPlate);
    return score;
  }
}
