package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.remarks.BehaviorRemark;
import seedu.address.model.person.remarks.ClassRemark;
import seedu.address.model.person.remarks.DietaryRemark;
import seedu.address.model.person.remarks.Remark;
import seedu.address.model.tag.Tag;

/**
 * Imports persons from a CSV file.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports persons from a CSV file. "
            + "Parameters: FILE_PATH\n"
            + "CSV columns (fixed indices): "
            + "0=name,1=age,2=address,3=parentName,4=parentPhone,5=parentEmail,"
            + "6=tags,7=remark,8=dietaryRemark,9=classRemark,10=behaviorRemark\n"
            + "Leave optional fields empty in CSV if not needed (for example: ',,').\n"
            + "If present, tags should be separated by semicolons ';'.\n"
            + "Example: " + COMMAND_WORD + " data/contacts.csv";

    public static final String MESSAGE_FILE_READ_ERROR = "Unable to read CSV file: %1$s";
    public static final String MESSAGE_CSV_INVALID_FORMAT = "Invalid CSV format at line %1$d: %2$s";
    public static final String MESSAGE_SUCCESS = "Imported %1$d person(s). Skipped %2$d duplicate person(s).";

    private static final int MIN_COLUMN_COUNT = 6;
    private static final int MAX_COLUMN_COUNT = 11;
    private static final int TAGS_COLUMN_INDEX = 6;
    private static final int REMARK_COLUMN_INDEX = 7;
    private static final int DIETARY_REMARK_COLUMN_INDEX = 8;
    private static final int CLASS_REMARK_COLUMN_INDEX = 9;
    private static final int BEHAVIOR_REMARK_COLUMN_INDEX = 10;

    private final Path csvFilePath;

    /**
     * Creates an ImportCommand to import persons from {@code csvFilePath}.
     */
    public ImportCommand(Path csvFilePath) {
        requireNonNull(csvFilePath);
        this.csvFilePath = csvFilePath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> importedPersons = readPersonsFromCsv(csvFilePath);
        int importedCount = 0;
        int skippedDuplicates = 0;

        for (Person person : importedPersons) {
            if (model.hasPerson(person)) {
                skippedDuplicates++;
                continue;
            }

            model.addPerson(person);
            importedCount++;
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, importedCount, skippedDuplicates));
    }

    private List<Person> readPersonsFromCsv(Path path) throws CommandException {
        final List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            throw new CommandException(String.format(MESSAGE_FILE_READ_ERROR, path), ioe);
        }

        List<Person> persons = new ArrayList<>();
        boolean isFirstNonEmptyLine = true;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) {
                continue;
            }

            int lineNumber = i + 1;
            List<String> fields = splitCsvLine(line);

            if (isFirstNonEmptyLine && isHeader(fields)) {
                isFirstNonEmptyLine = false;
                continue;
            }

            isFirstNonEmptyLine = false;
            persons.add(parsePerson(fields, lineNumber));
        }

        return persons;
    }

    private Person parsePerson(List<String> fields, int lineNumber) throws CommandException {
        if (fields.size() < MIN_COLUMN_COUNT || fields.size() > MAX_COLUMN_COUNT) {
            throw new CommandException(String.format(MESSAGE_CSV_INVALID_FORMAT, lineNumber,
                    "Expected 6 to 11 columns but found " + fields.size()));
        }

        try {
            Name name = ParserUtil.parseName(fields.get(0));
            Age age = ParserUtil.parseAge(fields.get(1));
            Address address = ParserUtil.parseAddress(fields.get(2));
            Name parentName = ParserUtil.parseName(fields.get(3));
            Phone parentPhone = ParserUtil.parsePhone(fields.get(4));
            Email parentEmail = ParserUtil.parseEmail(fields.get(5));

            ParsedOptionalFields optionalFields = parseOptionalFields(fields);

            return new Person(name, age, address, optionalFields.tags,
                    parentName, parentPhone, parentEmail,
                    new Remark(optionalFields.remark),
                    new DietaryRemark(optionalFields.dietaryRemark),
                    new ClassRemark(optionalFields.classRemark),
                    new BehaviorRemark(optionalFields.behaviorRemark));
        } catch (ParseException pe) {
            throw new CommandException(String.format(MESSAGE_CSV_INVALID_FORMAT, lineNumber, pe.getMessage()), pe);
        }
    }

    private ParsedOptionalFields parseOptionalFields(List<String> fields) throws ParseException {
        Set<Tag> tags = new HashSet<>();
        String remark = "";
        String dietaryRemark = "";
        String classRemark = "";
        String behaviorRemark = "";

        if (fields.size() <= TAGS_COLUMN_INDEX) {
            return new ParsedOptionalFields(tags, remark, dietaryRemark, classRemark, behaviorRemark);
        }

        tags = parseTags(fields.get(TAGS_COLUMN_INDEX));
        remark = getOptionalField(fields, REMARK_COLUMN_INDEX);
        dietaryRemark = getOptionalField(fields, DIETARY_REMARK_COLUMN_INDEX);
        classRemark = getOptionalField(fields, CLASS_REMARK_COLUMN_INDEX);
        behaviorRemark = getOptionalField(fields, BEHAVIOR_REMARK_COLUMN_INDEX);

        return new ParsedOptionalFields(tags, remark, dietaryRemark, classRemark, behaviorRemark);
    }

    private String getOptionalField(List<String> fields, int index) {
        if (fields.size() <= index) {
            return "";
        }
        return fields.get(index);
    }

    private Set<Tag> parseTags(String rawTags) throws ParseException {
        Set<Tag> tags = new HashSet<>();

        if (rawTags == null || rawTags.trim().isEmpty()) {
            return tags;
        }

        String[] splitTags = rawTags.split(";");
        for (String splitTag : splitTags) {
            String trimmedTag = splitTag.trim();
            if (trimmedTag.isEmpty()) {
                continue;
            }
            tags.add(ParserUtil.parseTag(trimmedTag));
        }
        return tags;
    }

    private List<String> splitCsvLine(String line) throws CommandException {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (currentChar == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
                continue;
            }

            currentField.append(currentChar);
        }

        if (inQuotes) {
            throw new CommandException("CSV row has unclosed quoted field");
        }

        fields.add(currentField.toString().trim());
        return fields;
    }

    private boolean isHeader(List<String> fields) {
        if (fields.size() < MIN_COLUMN_COUNT) {
            return false;
        }

        return normalized(fields.get(0)).equals("name")
                && normalized(fields.get(1)).equals("age")
                && normalized(fields.get(2)).equals("address")
                && normalized(fields.get(3)).equals("parentname")
                && normalized(fields.get(4)).equals("parentphone")
                && normalized(fields.get(5)).equals("parentemail");
    }

    private String normalized(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }

    private static class ParsedOptionalFields {
        private final Set<Tag> tags;
        private final String remark;
        private final String dietaryRemark;
        private final String classRemark;
        private final String behaviorRemark;

        private ParsedOptionalFields(Set<Tag> tags, String remark, String dietaryRemark,
                                     String classRemark, String behaviorRemark) {
            this.tags = tags;
            this.remark = remark;
            this.dietaryRemark = dietaryRemark;
            this.classRemark = classRemark;
            this.behaviorRemark = behaviorRemark;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ImportCommand)) {
            return false;
        }

        ImportCommand otherImportCommand = (ImportCommand) other;
        return csvFilePath.equals(otherImportCommand.csvFilePath);
    }

    @Override
    public String toString() {
        return ImportCommand.class.getCanonicalName() + "{csvFilePath=" + csvFilePath + "}";
    }
}