package praty.springx.theme;

/**
 * Terminal color and symbol palette.
 */
public interface Theme {

    String name();

    String info(String text);

    String success(String text);

    String warn(String text);

    String error(String text);

    String secondary(String text);

    String accent(String text);

    String iconSuccess();

    String iconError();

    String iconWarn();

    String iconInfo();

    String divider();
}
