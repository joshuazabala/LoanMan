import { EnumSessionStatus } from "../common/EnumSessionStatus";

export default class Session {

    public id: string;
    public userId: number;
    public username: string;
    public status: EnumSessionStatus;
    public loginTime: string;
    public lastActivityTime: string;
    public origin: string;
    public remarks: string;

    public constructor() {
        this.id = "";
        this.userId = 0;
        this.username = "";
        this.status = EnumSessionStatus.ACTIVE;
        this.loginTime = "";
        this.lastActivityTime = "";
        this.origin = "";
        this.remarks = "";
    }

}