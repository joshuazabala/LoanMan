export default class GroupType {

    public id: number;
    public active: boolean;
    public name: string;
    public description: string;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.name = "";
        this.description = "";
    }

}