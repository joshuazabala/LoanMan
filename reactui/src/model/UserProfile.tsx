import { EnumModuleAccessId } from "../common/EnumModuleAccessId";

export default class UserProfile {

    public id: number;
    public active: boolean;

    public name: string;
    public description: string;

    public moduleAccessIds: Array<EnumModuleAccessId>;

    public constructor() {
        this.id = 0;
        this.active = true;

        this.name = "";
        this.description = "";

        this.moduleAccessIds = new Array<EnumModuleAccessId>();
    }

}