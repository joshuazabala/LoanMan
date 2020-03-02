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

}