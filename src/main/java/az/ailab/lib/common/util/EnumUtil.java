package az.ailab.lib.common.util;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utility class for working with Java Enums.
 * <p>
 * This class provides methods to retrieve enum constants safely by name,
 * supporting both case-sensitive and case-insensitive lookups.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Retrieve an enum constant by its name.</li>
 *     <li>Handle case-insensitive lookups.</li>
 *     <li>Return an optional value to prevent null pointer exceptions.</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 * <pre>
 * enum Color {
 *     RED, GREEN, BLUE
 * }
 *
 * // Retrieve an enum constant directly (returns null if not found)
 * Color color = EnumUtil.getEnumConstant(Color.class, "red"); // Returns Color.RED
 * Color invalidColor = EnumUtil.getEnumConstant(Color.class, "yellow"); // Returns null
 *
 * // Retrieve an enum constant safely using Optional
 * Optional&lt;Color&gt; optionalColor = EnumUtil.getOptEnumConstant(Color.class, "green");
 * optionalColor.ifPresent(value -> System.out.println("Found: " + value)); // Prints "Found: GREEN"
 * </pre>
 * <p><strong>Note:</strong> This is a utility class and cannot be instantiated.</p>
 */
public final class EnumUtil {

    private EnumUtil() {
    }

    /**
     * Retrieves an enum constant by its name.
     * <p>
     * If the name does not match any constant, it returns {@code null}.
     * </p>
     *
     * @param enumType the enum class type.
     * @param name     the name of the enum constant (case-insensitive).
     * @param <T>      the type of the enum.
     * @return the matching enum constant, or {@code null} if not found.
     */
    public static <T extends Enum<T>> T getEnumConstant(Class<T> enumType, String name) {
        return getOptEnumConstant(enumType, name).orElse(null);
    }

    /**
     * Retrieves an enum constant by its name, wrapped in an {@link Optional}.
     * <p>
     * This method ensures a safe way to fetch enums without null checks.
     * </p>
     *
     * @param enumType the enum class type.
     * @param name     the name of the enum constant (case-insensitive).
     * @param <T>      the type of the enum.
     * @return an {@link Optional} containing the matching enum constant, or an empty {@link Optional} if not found.
     */
    public static <T extends Enum<T>> Optional<T> getOptEnumConstant(Class<T> enumType,
                                                                     String name) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(t -> t.name().equalsIgnoreCase(name))
                .findFirst();
    }

}