package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    private static final String SPLIT_BY_WHITESPACE = "\\s+";

    /**
     * Array of all prefixes that the FindCommand can search by.
     */
    private static final Prefix[] ALLOWED_PREFIXES = {
        PREFIX_NAME, PREFIX_ADDRESS, PREFIX_AGE, PREFIX_TAG,
        PREFIX_REMARK, PREFIX_DIETARY_REMARK, PREFIX_CLASS_REMARK,
        PREFIX_BEHAVIOR_REMARK, PREFIX_PARENT_NAME, PREFIX_PARENT_PHONE,
        PREFIX_PARENT_EMAIL
    };

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, ALLOWED_PREFIXES);
        Map<Prefix, List<String>> keywordsMap = new HashMap<>();

        // Handle Preamble
        String preamble = argMultimap.getPreamble().trim();
        if (!preamble.isEmpty()) {
            keywordsMap.put(PREFIX_NAME, new ArrayList<>(Arrays.asList(preamble.split(SPLIT_BY_WHITESPACE))));
        }

        // Process all other prefixes
        for (Prefix prefix : ALLOWED_PREFIXES) {
            if (argMultimap.getValue(prefix).isPresent()) {
                List<String> keywords = argMultimap.getAllValues(prefix).stream()
                        .flatMap(s -> Arrays.stream(s.trim().split(SPLIT_BY_WHITESPACE)))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (keywords.isEmpty()) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }

                // If it's a name prefix, merge with preamble keywords if they already exist
                if (prefix.equals(PREFIX_NAME)) {
                    List<String> existingNames = keywordsMap.getOrDefault(PREFIX_NAME, new ArrayList<>());
                    existingNames.addAll(keywords);
                    keywordsMap.put(PREFIX_NAME, existingNames);
                } else {
                    keywordsMap.put(prefix, keywords);
                }
            }
        }

        if (keywordsMap.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(new NameContainsKeywordsPredicate(keywordsMap));
    }
}
