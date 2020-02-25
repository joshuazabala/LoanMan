import { EnumCutoffFrequency } from "../common/EnumCutoffFrequency";

export default class CutoffProfile {

    public id: number;
    public active: boolean;
    public frequency: EnumCutoffFrequency;
    public code: string;
    public description: string;
    public firstHalfStart: number;
    public secondHalfStart: number;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.frequency = EnumCutoffFrequency.MONTHLY;
        this.code = "";
        this.description = "";
        this.firstHalfStart = 1;
        this.secondHalfStart = 16;
    }

}