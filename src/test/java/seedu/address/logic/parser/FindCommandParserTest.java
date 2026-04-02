package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // No prefixes - treat as student name keywords
        Map<Prefix, List<String>> nameMap = new HashMap<>();
        nameMap.put(PREFIX_NAME, Arrays.asList("Alice", "Bob"));
        FindCommand expectedFindCommand = new FindCommand(new NameContainsKeywordsPredicate(nameMap));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // Mixed Preamble and n/ prefix (Tests Merge Logic)
        Map<Prefix, List<String>> mixedMap = new HashMap<>();
        mixedMap.put(PREFIX_NAME, new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie")));
        FindCommand expectedMixedCommand = new FindCommand(new NameContainsKeywordsPredicate(mixedMap));
        assertParseSuccess(parser, "Alice Bob n/Charlie", expectedMixedCommand);

        // Multi-prefix search
        Map<Prefix, List<String>> multiMap = new HashMap<>();
        multiMap.put(PREFIX_AGE, Arrays.asList("12"));
        multiMap.put(PREFIX_PARENT_NAME, Arrays.asList("Tan"));
        FindCommand expectedMultiCommand = new FindCommand(new NameContainsKeywordsPredicate(multiMap));
        assertParseSuccess(parser, " a/12 pn/Tan", expectedMultiCommand);
    }

    @Test
    public void parse_invalidPrefix_throwsParseException() {
        // Unknown prefix pattern ending in /
        assertParseFailure(parser, " unknown/", "Unknown prefix detected: unknown/"
                + "\nPlease use only valid prefixes (n/, pn/, a/, etc.)");

        // Preamble containing a slash
        assertParseFailure(parser, " dave/ ", "Invalid prefix detected or unauthorized use of forward slash.");
    }

    @Test
    public void parse_invalidValue_throwsParseException() {
        // Invalid Age (non-numeric)
        assertParseFailure(parser, " a/twelve", Age.MESSAGE_CONSTRAINTS);

        // Invalid Name (numeric only)
        assertParseFailure(parser, " n/123", Name.MESSAGE_CONSTRAINTS);

        // Invalid Address (blank)
        assertParseFailure(parser, " ad/ ", Address.MESSAGE_CONSTRAINTS);

        // Blank prefix value
        assertParseFailure(parser, " pn/", String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
