package bg.softuni.webbookstore.model.entity.enums;

import bg.softuni.webbookstore.utils.StringUtils;

public enum LanguageEnum {
    BULGARIAN,
    ENGLISH,
    GERMAN,
    RUSSIAN;

    @Override
    public String toString() {
        return StringUtils.capitalizeEnum(name());
    }
}
