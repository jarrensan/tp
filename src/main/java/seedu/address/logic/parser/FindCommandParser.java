package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_NAME;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PARENT_NAME);

        Map<Prefix, List<String>> keywordsMap = new HashMap<>();

        boolean hasNamePrefix = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasParentPrefix = argMultimap.getValue(PREFIX_PARENT_NAME).isPresent();

        if (!hasNamePrefix && !hasParentPrefix) {
            // OPTION 1: Legacy search (No prefixes used)
            keywordsMap.put(PREFIX_NAME, Arrays.asList(trimmedArgs.split("\\s+")));
        } else {
            // OPTION 2: Prefix-based search
            List<String> nameKeywords = argMultimap.getAllValues(PREFIX_NAME);
            List<String> parentKeywords = argMultimap.getAllValues(PREFIX_PARENT_NAME);

            if (isAnyPrefixEmpty(nameKeywords, parentKeywords)) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }

            if (hasNamePrefix) {
                keywordsMap.put(PREFIX_NAME, nameKeywords);
            }
            if (hasParentPrefix) {
                keywordsMap.put(PREFIX_PARENT_NAME, parentKeywords);
            }
        }

        return new FindCommand(new NameContainsKeywordsPredicate(keywordsMap));
    }

    /**
     * Returns true if any of the provided lists are from prefixes but contain only empty strings.
     */
    private boolean isAnyPrefixEmpty(List<String> nameKeywords, List<String> parentKeywords) {
        return nameKeywords.stream().anyMatch(String::isEmpty)
                || parentKeywords.stream().anyMatch(String::isEmpty);
    }
}
