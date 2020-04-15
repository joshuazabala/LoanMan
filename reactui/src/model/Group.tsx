export default class Group {

    public id: number;
    public active: boolean;
    public name: string;
    public description: string;
    public groupTypeId: number;
    public groupType: string;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.name = "";
        this.description = "";
        this.groupTypeId = 0;
        this.groupType = "";
    }

}