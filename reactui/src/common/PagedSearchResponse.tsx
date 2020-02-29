import { EnumResponseStatus } from './EnumResponseStatus';

export default class PagedSearchResponse<T> {

    public content: T[];
    public totalPageCount: number;
    public status: EnumResponseStatus;
    public message: string;

    public constructor() {
        this.content = new Array<T>();
        this.totalPageCount = 0;
        this.status = EnumResponseStatus.SUCCESSFUL;
        this.message = "";
    }

}