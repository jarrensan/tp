package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_NAME;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;
import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        Map<Prefix, List<String>> firstPredicateMap = new HashMap<>();
        firstPredicateMap.put(PREFIX_NAME, Collections.singletonList("first"));

        Map<Prefix, List<String>> secondPredicateMap = new HashMap<>();
        secondPredicateMap.put(PREFIX_NAME, Arrays.asList("first", "second"));

        Map<Prefix, List<String>> parentPredicateMap = new HashMap<>();
        parentPredicateMap.put(PREFIX_PARENT_NAME, Collections.singletonList("first"));

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateMap);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateMap);
        NameContainsKeywordsPredicate parentPredicate = new NameContainsKeywordsPredicate(parentPredicateMap);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateMap);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // different prefix (Name vs Parent Name) -> returns false
        assertFalse(firstPredicate.equals(parentPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword (using legacy constructor wrapper)
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords (explicit Map)
        Map<Prefix, List<String>> keywordsMap = new HashMap<>();
        keywordsMap.put(PREFIX_NAME, Arrays.asList("Alice", "Bob"));
        predicate = new NameContainsKeywordsPredicate(keywordsMap);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Parent Name match
        keywordsMap = new HashMap<>();
        keywordsMap.put(PREFIX_PARENT_NAME, Collections.singletonList("Tan"));
        predicate = new NameContainsKeywordsPredicate(keywordsMap);
        // Note: Ensure your PersonBuilder has the .withParentName() method
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withParentName("Tan Ah Teck").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords (Legacy constructor)
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void toStringMethod() {
        Map<Prefix, List<String>> keywordsMap = new HashMap<>();
        keywordsMap.put(PREFIX_NAME, List.of("keyword1"));
        keywordsMap.put(PREFIX_PARENT_NAME, List.of("keyword2"));
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywordsMap);

        // Map.toString() order can vary in some Java versions,
        // but ToStringBuilder handles the formatting for us.
        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywordsMap=" + keywordsMap + "}";
        assertEquals(expected, predicate.toString());
    }
}
