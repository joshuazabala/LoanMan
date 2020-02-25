export default class Client {
 
    public id: number;
    public active: boolean;
    public clientNumber: string;
    public lastName: string;
    public firstName: string;
    public middleName: string;
    public contactNumber: string;
    public emailAddress: string;
    public address: string;

    public constructor() {
        this.id = 0;
        this.active = true;
        this.clientNumber = "";
        this.lastName = "";
        this.firstName = "";
        this.middleName = "";
        this.contactNumber = "";
        this.emailAddress = "";
        this.address = "";
    }

}