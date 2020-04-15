import { EnumCutoffFrequency } from "../common/EnumCutoffFrequency";

export default class LoanType {

    public id: number;
    public active: boolean;
    public name: string;
    public description: string;
    public paymentFrequency: EnumCutoffFrequency;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.name = "";
        this.description = "";
        this.paymentFrequency = EnumCutoffFrequency.MONTHLY;
    }

}