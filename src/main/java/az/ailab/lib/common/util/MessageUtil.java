package az.ailab.lib.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for resolving message templates with dynamic arguments.
 * <p>
 * This class provides methods to format message strings dynamically by replacing
 * placeholders ({}) with provided arguments.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * {@code
 * String message = MessageUtil.resolveMessage("Hello, {}!", "John");
 * System.out.println(message); // Output: "Hello, John!"
 * }
 * </pre>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li> Handles blank messages and missing arguments gracefully.</li>
 *     <li> Supports message formatting using placeholders (`{}`).</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This is a utility class and cannot be instantiated.</p>
 */
public final class MessageUtil {

    private MessageUtil() {
    }

    /**
     * Resolves a message template by replacing placeholders (`{}`) with provided arguments.
     * <p>
     * If the message is blank but arguments exist, the first argument is used as the message.
     * If there are no arguments, it returns the original message.
     * </p>
     *
     * @param message The message template containing placeholders (`{}`).
     * @param args    The arguments to replace placeholders in the message.
     * @return A formatted message with arguments inserted, or {@code null} if both message and arguments are empty.
     */
    public static String resolveMessage(String message, Object... args) {
        boolean isBlankMessage = StringUtils.isBlank(message);
        boolean isEmptyArgs = (ArrayUtils.isEmpty(args) || args[0] == null);

        if (isBlankMessage && isEmptyArgs) {
            return null;
        }

        if (isBlankMessage) {
            message = String.valueOf(args[0]);

            if (args.length == 1) {
                return message;
            }

            args = ArrayUtils.subarray(args, 1, args.length);
        } else if (isEmptyArgs) {
            return message;
        }

        return bindArguments(message, args);
    }

    /**
     * Replaces placeholders (`{}`) in a message template with the provided arguments.
     * <p>
     * If the number of placeholders is less than the number of arguments,
     * extra arguments are ignored. If there are more placeholders than arguments,
     * the remaining placeholders remain unchanged.
     * </p>
     *
     * @param message The message template.
     * @param args    The arguments to be inserted into the template.
     * @return A formatted string with replaced placeholders.
     */
    private static String bindArguments(String message, Object[] args) {
        StringBuilder sb = new StringBuilder(message);
        String placeHolder = "{}";

        for (Object arg : args) {
            int index = sb.indexOf(placeHolder);

            if (index == -1) {
                break;
            }

            sb.replace(index, index + placeHolder.length(), String.valueOf(arg));
        }

        return sb.toString();
    }

}

