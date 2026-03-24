package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;
import java.util.Map;
import java.util.HashMap;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.parser.Prefix;

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

/**
 * Tests that a {@code Person}'s attributes match any of the keywords given for specified prefixes.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final Map<Prefix, List<String>> keywordsMap;

    /**
     * Constructs a {@code NameContainsKeywordsPredicate} that only filters by the person's name.
     * The parent keywords list will be initialized as empty.
     *
     * @param keywords A list of strings to match against a person's name.
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywordsMap = new HashMap<>();
        if (!keywords.isEmpty()) {
            this.keywordsMap.put(PREFIX_NAME, keywords);
        }
    }

    /**
     * Constructs a {@code NameContainsKeywordsPredicate} with specific keywords for multi-parameter
     * searches
     *
     * @param keywordsMap A map of keywords
     */
    public NameContainsKeywordsPredicate(Map<Prefix, List<String>> keywordsMap) {
        this.keywordsMap = keywordsMap;
    }

    @Override
    public boolean test(Person person) {
        // Returns true if ANY prefix in the map has a keyword that matches the person's corresponding field.
        return keywordsMap.entrySet().stream().anyMatch(entry -> {
            Prefix prefix = entry.getKey();
            List<String> keywords = entry.getValue();
            String fieldToSearch = getFieldByPrefix(prefix, person);

            return keywords.stream().anyMatch(keyword ->
                    StringUtil.containsWordIgnoreCase(fieldToSearch, keyword));
        });
    }

    /**
     * Helper method to map a Prefix to a specific field in the Person object.
     */
    private String getFieldByPrefix(Prefix prefix, Person person) {
        if (prefix.equals(PREFIX_NAME)) return person.getName().fullName;
        if (prefix.equals(PREFIX_AGE)) return person.getAge().value;
        if (prefix.equals(PREFIX_ADDRESS)) return person.getAddress().value;
        if (prefix.equals(PREFIX_PARENT_NAME)) return person.getParentName().fullName;
        if (prefix.equals(PREFIX_PARENT_PHONE)) return person.getParentPhone().value;
        if (prefix.equals(PREFIX_PARENT_EMAIL)) return person.getParentEmail().value;
        if (prefix.equals(PREFIX_REMARK)) return person.getRemark().value;
        if (prefix.equals(PREFIX_DIETARY_REMARK)) return person.getDietaryRemark().value;
        if (prefix.equals(PREFIX_CLASS_REMARK)) return person.getClassRemark().value;
        if (prefix.equals(PREFIX_BEHAVIOR_REMARK)) return person.getBehaviorRemark().value;

        if (prefix.equals(PREFIX_TAG)) {
            return person.getTags().stream()
                    .map(tag -> tag.tagName)
                    .reduce("", (acc, tag) -> acc + " " + tag);
        }

        return "";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof NameContainsKeywordsPredicate)) {
            return false;
        }

        NameContainsKeywordsPredicate otherPredicate = (NameContainsKeywordsPredicate) other;
        return keywordsMap.equals(otherPredicate.keywordsMap);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywordsMap", keywordsMap)
                .toString();
    }
}
