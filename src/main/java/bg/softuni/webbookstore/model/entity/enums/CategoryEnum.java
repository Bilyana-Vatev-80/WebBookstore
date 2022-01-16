package bg.softuni.webbookstore.model.entity.enums;

import bg.softuni.webbookstore.utils.StringUtils;

public enum CategoryEnum {
    FICTION,
    CRIME_AND_THRILLER,
    ROMANCE,
    FANTASY,
    SCIENCE_FICTION,
    SCIENCE,
    CLASSICS,
    CHILDREN,
    HISTORY,
    ART,
    DYSTOPIA,
    HORROR,
    POETRY;

    @Override
    public String toString() {
       return StringUtils.capitalizeEnum(name());
    }
}
