export default class Payment {

    public id: number;
    public active: boolean;
    public date: string;
    public amount: number;
    public remarks: string;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.date = "";
        this.amount = 0;
        this.remarks = "";
    }

}