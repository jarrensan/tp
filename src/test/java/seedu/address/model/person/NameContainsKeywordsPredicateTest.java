package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BEHAVIOR_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DIETARY_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;
import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void constructor_listOnly_initializesWithNamePrefix() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    public void equals() {
        Map<Prefix, List<String>> firstMap = Map.of(PREFIX_NAME, List.of("first"));
        Map<Prefix, List<String>> secondMap = Map.of(PREFIX_NAME, List.of("first", "second"));

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstMap);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondMap);

        assertTrue(firstPredicate.equals(firstPredicate));
        NameContainsKeywordsPredicate firstCopy = new NameContainsKeywordsPredicate(firstMap);
        assertTrue(firstPredicate.equals(firstCopy));
        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_attributeMatches_returnsTrue() {
        // Substring matching for email
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(
                Map.of(PREFIX_PARENT_EMAIL, List.of("alex")));
        assertTrue(predicate.test(new PersonBuilder().withParentEmail("alexyeoh@gmail.com").build()));

        // OR logic across prefixes
        predicate = new NameContainsKeywordsPredicate(Map.of(
                PREFIX_NAME, List.of("Alice"),
                PREFIX_AGE, List.of("15")
        ));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withAge("20").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob").withAge("15").build()));

        // Case insensitivity
        predicate = new NameContainsKeywordsPredicate(Map.of(PREFIX_NAME, List.of("alice")));
        assertTrue(predicate.test(new PersonBuilder().withName("ALICE").build()));
    }

    @Test
    public void test_attributeNoMatch_returnsFalse() {
        // Age Exact Match: "1" should NOT match "12"
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Map.of(PREFIX_AGE, List.of("1")));
        assertFalse(predicate.test(new PersonBuilder().withAge("12").build()));

        // Prefix mismatch
        predicate = new NameContainsKeywordsPredicate(Map.of(PREFIX_NAME, List.of("Main")));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withAddress("Main Street").build()));
    }

    @Test
    public void test_getFieldByPrefix_allBranches() {
        Person person = new PersonBuilder()
                .withName("Alice").withAge("10").withAddress("Street")
                .withParentName("Papa").withParentPhone("999").withParentEmail("a@b.com")
                .withRemark("R1").withDietaryRemark("D1").withClassRemark("C1")
                .withBehaviorRemark("B1").withTags("T1").build();

        Prefix[] prefixes = {
                PREFIX_NAME, PREFIX_AGE, PREFIX_ADDRESS, PREFIX_PARENT_NAME,
                PREFIX_PARENT_PHONE, PREFIX_PARENT_EMAIL, PREFIX_REMARK,
                PREFIX_DIETARY_REMARK, PREFIX_CLASS_REMARK, PREFIX_BEHAVIOR_REMARK, PREFIX_TAG
        };

        for (Prefix p : prefixes) {
            NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Map.of(p, List.of(" ")));
            predicate.test(person); // Hits all branches in getFieldByPrefix
        }
    }

    @Test
    public void toStringMethod() {
        Map<Prefix, List<String>> keywordsMap = Map.of(PREFIX_NAME, List.of("keyword"));
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywordsMap);
        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywordsMap=" + keywordsMap + "}";
        assertEquals(expected, predicate.toString());
    }
}
