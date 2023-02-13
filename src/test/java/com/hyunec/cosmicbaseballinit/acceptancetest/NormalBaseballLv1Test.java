package com.hyunec.cosmicbaseballinit.acceptancetest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.hyunec.cosmicbaseballinit.domain.baseball.model.Batting;
import com.hyunec.cosmicbaseballinit.domain.baseball.model.BattingResult;
import com.hyunec.cosmicbaseballinit.domain.baseball.model.exception.ExceptionMessage;
import com.hyunec.cosmicbaseballinit.domain.baseball.model.service.BaseballManagerImpl;
import com.hyunec.cosmicbaseballinit.domain.baseball.model.utils.generator.RandomBattingGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NormalBaseballLv1Test {

  @InjectMocks
  private BaseballManagerImpl baseballService;

  @Spy
  private RandomBattingGenerator battingGenerator;

  @DisplayName("strike, ball, hit 는 같은 확률 입니다.")
  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2})
  void t1(int number) {
    //given
    int MAX_NUMBER = 3;
    given(battingGenerator.getRandomNumber(Batting.getBattingSize())).willReturn(number);

    //when
    Batting generateValue = battingGenerator.generator();

    //then
    assertThat(generateValue).isEqualTo(Batting.of(number % MAX_NUMBER));
  }

  @DisplayName("3B 타석에서 타격 결과가 ball 이면 타석 결과는 four_ball 됩니다.")
  @Test
  void t2() {
    //given
    given(battingGenerator.generator()).willReturn(Batting.BALL);
    given(battingGenerator.getRandomNumber(10)).willReturn(10);
    baseballService.batting();
    baseballService.batting();
    baseballService.batting();

    //when
    baseballService.batting();

    //then
    assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.FOUR_BALL);
  }

  @DisplayName("2S 타석에서 타격 결과가 strike 이면 타석 결과는 out 됩니다.")
  @Test
  void t3() {
    //given
    given(battingGenerator.generator()).willReturn(Batting.STRIKE);
    given(battingGenerator.getRandomNumber(10)).willReturn(10);
    baseballService.batting();
    baseballService.batting();

    //when
    baseballService.batting();

    //then
    assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.OUT);

  }

  @DisplayName("진행 중인 타석이 있는 상태에서 새로운 타석을 진행할 수 없습니다.")
  @Test
  void t4() {
    //given
    given(battingGenerator.generator()).willReturn(Batting.BALL);
    given(battingGenerator.getRandomNumber(10)).willReturn(10);
    baseballService.batting();

    //when, then
    assertThatThrownBy(() -> baseballService.newGame())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(ExceptionMessage.FAILURE_PLATE_IS_EXIST_DO_NOT_PLAY_NEW_GAME);
  }

  @DisplayName("타석이 종료되면 초기화하여 새로 진행할 수 있습니다.")
  @Test
  void t5() {
    //given
    given(battingGenerator.generator()).willReturn(Batting.STRIKE);
    given(battingGenerator.getRandomNumber(10)).willReturn(10);
    baseballService.batting();
    baseballService.batting();
    baseballService.batting();

    //when
    baseballService.newGame();

    //then
    assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.PLAYING);
  }

  @Nested
  class PlateAppearancesTest {
    @DisplayName("타석 결과 - 아웃")
    @Test
    void atBatResultOut() {
      // given
      given(battingGenerator.generator()).willReturn(Batting.STRIKE);
      given(battingGenerator.getRandomNumber(10)).willReturn(10);
      baseballService.batting();
      baseballService.batting();


      // when
      assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.STRIKE);
      baseballService.batting();

      // then
      assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.OUT);
    }

    @DisplayName("타석 결과 - 포볼")
    @Test
    void atBatResultsFourBall() {
      // given
      given(battingGenerator.generator()).willReturn(Batting.BALL);
      given(battingGenerator.getRandomNumber(10)).willReturn(10);
      baseballService.batting();
      baseballService.batting();
      baseballService.batting();

      // when
      assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.BALL);
      baseballService.batting();

      // then
      assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.FOUR_BALL);
    }

    @DisplayName("타석 결과 - 안타")
    @Test
    void atBatResultHits() {
      // given
      given(battingGenerator.generator()).willReturn(Batting.HIT);
      given(battingGenerator.getRandomNumber(10)).willReturn(10);

      // when
      baseballService.batting();

      // then
      assertThat(baseballService.getBattingResult()).isEqualTo(BattingResult.HIT);
    }
  }
}
