export default class Group {

    public id: number;
    public active: boolean;
    public code: string;
    public description: string;
    public groupTypeId: number;
    public groupType: string;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.code = "";
        this.description = "";
        this.groupTypeId = 0;
        this.groupType = "";
    }

}