export default class RequestContainer<T> {

    public content: T;
    public otherData: Map<string, any>;

    public constructor() {
        this.content = {} as T;
        this.otherData = new Map<string, any>();
    }

}