export default class GroupType {

    public id: number;
    public active: boolean;
    public code: string;
    public description: string;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.code = "";
        this.description = "";
    }

}