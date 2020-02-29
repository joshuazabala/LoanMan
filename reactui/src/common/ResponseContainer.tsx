import { EnumResponseStatus } from "./EnumResponseStatus";

export default class ResponseContainer<T> {

    public content: T;
    public status: EnumResponseStatus;
    public errorMap: Map<string, string>;
    public message: string;

    public constructor() {
        this.content = {} as T;
        this.status = EnumResponseStatus.SUCCESSFUL;
        this.errorMap = new Map<string, string>();
        this.message = "";
    }

}