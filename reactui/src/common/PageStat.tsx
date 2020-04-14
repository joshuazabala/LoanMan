export default class PageStat {

    public currentPage: number;
    public pageSize: number;
    public totalPageCount: number;
    public columnSorting: Map<string, "ascending" | "descending" | undefined>;
    public showInactive: boolean;

    public constructor() {
        this.currentPage = 1;
        this.pageSize = 20;
        this.totalPageCount = 0;
        this.columnSorting = new Map<string, "ascending" | "descending" | undefined>();            
        this.showInactive = false;
    }

}