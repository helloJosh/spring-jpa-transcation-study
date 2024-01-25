package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    void outerTxOff_success(){
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception(runtime)
     */
    @Test
    void outerTxOff_fail(){
        // given
        String username = "로그예외 outerTxOff_fail";

        // when
        assertThatThrownBy(()-> memberService.joinV1(username))
                        .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:OFF
     * logRepository    @Transactional:OFF
     */
    @Test
    void singleTx(){
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    void outerTxOn_success(){
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception(runtime)
     */
    @Test
    void outerTxOn_fail(){
        // given
        String username = "로그예외 outerTxOn_fail";

        // when
        assertThatThrownBy(()-> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception(runtime)
     */
    @Test
    void recoverException_fail(){
        // given
        String username = "로그예외 recoverException_fail";

        // when
        assertThatThrownBy(()-> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON(REQUIRES_NEW) Exception(runtime)
     */
    @Test
    void recoverException_sucess(){
        // given
        String username = "로그예외 recoverException_sucess";

        // when
        memberService.joinV2(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

}