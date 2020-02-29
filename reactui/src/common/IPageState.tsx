import PageStat from './PageStat';

export default interface IPageState<T> {

    contents: T[],
    name: string,
    formVisible: boolean,
    selectedId: number,
    loading: boolean,
    queryString: string,
    pageStat: PageStat,
    initialized: boolean

}