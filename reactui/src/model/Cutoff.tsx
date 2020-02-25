import { EnumCutoffStatus } from "../common/EnumCutoffStatus";

export default class Cutoff {

    public id: number;
    public status: EnumCutoffStatus;
    public startDate: string;
    public endDate: string;
    public year: number;
    public month: number;
    public cutoffNumber: number;

    public constructor() {
        this.id = 0;
        this.status = EnumCutoffStatus.DRAFT;
        this.startDate = "";
        this.endDate = "";
        this.year = 1992;
        this.month = 8;
        this.cutoffNumber = 2;
    }

}