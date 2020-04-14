export default class Util {

    public static getMapValues = (map: Map<any, string>) => {
        let values = new Array<string>();
        map.forEach((item, key) => {
            values.push(item);
        });
        return values;
    }

    public static isBlankOrNullString = (text: string) => {
        if (text === undefined || text === null) {
            return true;
        }
        if (text.trim().length === 0) {
            return true;
        }
        return false;
    }

    public static objectToMap = (model: any) => {
        const map = new Map<string, string>();
        Object.keys(model).forEach(key => {
            map.set(key, model[key]);
        });
        return map;
    }

    public static columnSortingMapToObject = (map: Map<string, "ascending" | "descending" | undefined>) => {
        const mapObject: { [key: string]: "ascending" | "descending" | undefined } = {};
        map.forEach((value, key) => {
            mapObject[key] = value;
        });
        return mapObject;
    }

    public static objectToColumnSortingMap = (model: any) => {
        const columnSortingMap = new Map<string, "ascending" | "descending" | undefined>();
        Object.keys(model).forEach(key => {
            const value = model[key] === "ascending" ? "ascending" : model[key] === "descending" ? "descending" : undefined;
            columnSortingMap.set(key, value);
        });
        return columnSortingMap;
    }

}