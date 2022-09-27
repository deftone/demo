package de.deftone.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipantTest {


    @ParameterizedTest
    @CsvSource({
            "\\IrgendwaS, -IrgendwaS",
            "irgendwas/, irgendwas-",
            "'irgend'was', -irgend-was-",
            "\"irgendwas\", -irgendwas-",
            "$irgendwas*, -irgendwas-",
            "~irgendwas^, -irgendwas-",
            "!irgendwas?, -irgendwas-",
            "<irgendwas>, -irgendwas-",
            "irgendwas@, irgendwas-",
            "umlautOköÖäÄüÜ, umlautOköÖäÄüÜ",
            "Kathrin & Katrin, Kathrin & Katrin",
            "Kathrin && Katrin, Kathrin && Katrin",
            "Team Kat(h)rin, Team Kat(h)rin"
    })
    void check(String expression, String expected) {
        assertEquals(expected, Participant.cleanUserInput(expression));
    }

    @ParameterizedTest
    @CsvSource({
            "select, --",
            "insert, --",
            "DROP, --",
            "UPDATE, --",
            "DeLaY, --",
            " 1' OR 2+776-776-1=0+0+0+1 or 'EryZMk78'=' , 1- OR 2-776-776-1-0-0-0-1 or -EryZMk78---",
            "-1\\\" OR 2+844-844-1=0+0+0+1 -- \" , -1-- OR 2-844-844-1-0-0-0-1 -- -",
            " if(now()=sysdate() , if(now()-sysdate()",
            " -1; waitfor delay '0:0:15' -- , -1- waitfor -- -0-0-15- --",
            " (select(0)from(select(sleep(15)))v)/*'+(select(0)from(select(sleep(15)))v)+'\"+(select(0)from(select(sleep(15)))v)+\"*/ " +
                    ", ----0-from-------15---v---------0-from-------15---v---------0-from-------15---v-----"
    })
    void realData(String expression, String expected) {
        assertEquals(expected, Participant.cleanUserInput(expression));
    }

    @Test
    void kommasTesten() {
        String expression = "#0'XOR(if(now()=sysdate(),Sleep(15),0))XOR'Z";
        String expected__ = "-0-xor-if-now---sysdate------15--0--xor-z";
        assertEquals(expected__, Participant.cleanUserInput(expression));
    }

}