export default class PageStat {

    public currentPage: number;
    public pageSize: number;
    public totalPageCount: number;

    public constructor() {
        this.currentPage = 1;
        this.pageSize = 20;
        this.totalPageCount = 0;
    }

}