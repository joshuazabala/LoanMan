export default class PagedSearchRequest {

    public queryString: string;
    public includeInactive: boolean;
    public pageNumber: number;
    public pageSize: number;
    public otherData: any;
    public columnSorting: object;

    public constructor() {
        this.queryString = "";
        this.includeInactive = false;
        this.pageNumber = 1;
        this.pageSize = 20;

        this.otherData = {};
        this.columnSorting = {};
    }

}