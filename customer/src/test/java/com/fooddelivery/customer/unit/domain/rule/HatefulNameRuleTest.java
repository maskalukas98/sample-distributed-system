package com.fooddelivery.customer.unit.domain.rule;

import com.fooddelivery.customer.domain.rule.hatefulname.HatefulNameRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class HatefulNameRuleTest {
    @ParameterizedTest
    @ValueSource(
            strings = {
                    "pofuckwo",
                    "lukfuckapp",
                    "killlukas",
                    "lukaskill",
                    "memekillmeme",
                    "whodick",
                    "whadickeee"
            }
    )
    public void testValidate_shouldForbidInvalidName(String name) {
        // execute
        boolean isNameHateful = HatefulNameRule.isValid(name);

        // verify
        Assertions.assertFalse(isNameHateful);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "lukas",
                    "maska",
                    "jan",
                    "sara",
                    "leo",
                    "novak",
            }
    )
    public void testValidate_shouldAllowValidName(String name) {
        // execute
        boolean isNameHateful = HatefulNameRule.isValid(name);

        // verify
        Assertions.assertTrue(isNameHateful);
    }
}
