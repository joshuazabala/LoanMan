import { EnumLoanStatus } from "../common/EnumLoanStatus";

export default class Loan {

    public id: number;
    public status: EnumLoanStatus;
    public principal: number;
    public payable: number;
    public amortization: number;
    public loanDate: string;
    public paymentStartDate: string;
    public remarks: string;

    public clientId: string;
    public client: string;

    public loanTypeId: number;
    public loanType: string;

    public constructor() {
        this.id = 0;
        this.status = EnumLoanStatus.ACTIVE;
        this.principal = 0;
        this.payable = 0;
        this.amortization = 0;
        this.loanDate = "";
        this.paymentStartDate = "";
        this.remarks = "";

        this.clientId = "";
        this.client = "";

        this.loanTypeId = 0;
        this.loanType = "";
    }

}