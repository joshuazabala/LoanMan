export default class User {

    public id: number;
    public active: boolean;

    public username: string;

    public profileId: number;
    public profile: string;

    public firstName: string;
    public middleName: string;
    public lastName: string;

    public contactNumber: string;
    public emailAddress: string;

    public constructor() {
        this.id = 0;
        this.active = true;

        this.username = "";
        
        this.profileId = 0;
        this.profile = "";
        
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";

        this.contactNumber = "";
        this.emailAddress = "";
    }

}