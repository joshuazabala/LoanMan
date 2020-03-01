export default class Client {
 
    public id: string;
    public active: boolean;
    public lastName: string;
    public firstName: string;
    public middleName: string;
    public contactNumber: string;
    public emailAddress: string;
    public address: string;

    public constructor() {
        this.id = "";
        this.active = true;
        this.lastName = "";
        this.firstName = "";
        this.middleName = "";
        this.contactNumber = "";
        this.emailAddress = "";
        this.address = "";
    }

}