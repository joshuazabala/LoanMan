import * as React from 'react';
import {
    Button,
    ButtonProps,
    Container,
    DropdownItemProps,
    Form,
    FormInput,
    FormProps,
    Grid,
    GridColumn,
    GridRow,
    Header,
    Icon,
    InputOnChangeData,
    Loader,
    Pagination,
    PaginationProps,
    Table,
    TableBody,
    TableCell,
    TableFooter,
    TableHeader,
    TableHeaderCell,
    TableRow,
} from 'semantic-ui-react';

import { EnumCommonAction } from '../../common/EnumCommonAction';
import { EnumCutoffFrequency } from '../../common/EnumCutoffFrequency';
import { EnumCutoffStatus } from '../../common/EnumCutoffStatus';
import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchGet, fetchGetNoReturn, fetchPost } from '../../common/Request';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Cutoff from '../../model/Cutoff';
import DeleteRestoreDialog from '../common/DeleteRestoreDialog';
import CutoffFilterDialog from './CutoffFilterDialog';
import CutoffForm from './CutoffForm';

interface IState extends IPageState<Cutoff> {
    deleteRestoreModalVisible: boolean,
    frequency: EnumCutoffFrequency
    frequencies: DropdownItemProps[],
    filterDialogVisible: boolean,
    filterByYear: boolean,
    year: number
}

export default class CutoffPage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<Cutoff>(),
        formVisible: false,
        initialized: false,
        loading: false,
        name: "cutoffPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedId: 0,
        deleteRestoreModalVisible: false,
        frequency: EnumCutoffFrequency.UNKNOWN,
        frequencies: [
            { key: EnumCutoffFrequency.MONTHLY, value: EnumCutoffFrequency.MONTHLY, text: "Monthly" },
            { key: EnumCutoffFrequency.SEMI_MONTHLY, value: EnumCutoffFrequency.SEMI_MONTHLY, text: "Semi-Monthly" }
        ],
        filterDialogVisible: false,
        filterByYear: false,
        year: new Date().getFullYear()
    }

    public constructor(props: any) {
        super(props);

        this.monthMap = new Map<number, string>();
        this.monthMap.set(1, "January");
        this.monthMap.set(2, "February");
        this.monthMap.set(3, "March");
        this.monthMap.set(4, "April");
        this.monthMap.set(5, "May");
        this.monthMap.set(6, "June");
        this.monthMap.set(7, "July");
        this.monthMap.set(8, "August");
        this.monthMap.set(9, "September");
        this.monthMap.set(10, "October");
        this.monthMap.set(11, "November");
        this.monthMap.set(12, "December");
    }

    private formRef = React.createRef<CutoffForm>();
    private monthMap: Map<number, string>;
    private deleteRestoreDialogRef = React.createRef<DeleteRestoreDialog>();
    private filterDialogRef = React.createRef<CutoffFilterDialog>();

    public componentDidMount() {
        if (!this.state.initialized) {
            const pageStat = this.state.pageStat;
            pageStat.columnSorting.set("startDate", "descending");
            pageStat.columnSorting.set("frequency", "descending");
            this.setState(
                { initialized: true, pageStat },
                () => {
                    this.search();
                }
            );
        }
    }

    public render() {
        let selectedActive = true;
        let selectedPosted = false;
        if (this.state.selectedId !== 0) {
            const selectedItem = this.state.contents.filter(item => item.id === this.state.selectedId)[0];
            selectedActive = selectedItem.active;
            selectedPosted = selectedItem.status === EnumCutoffStatus.POSTED;
        }
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Cutoffs</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={2} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={8}>
                            <Button icon="add" primary={true} content="New" disabled={this.state.loading} onClick={this.onAdd} />
                            <Button icon="edit" primary={true} content="Edit" disabled={this.state.loading || this.state.selectedId === 0} onClick={this.onEdit} />
                            {
                                selectedActive &&
                                <Button
                                    icon="trash"
                                    negative={true}
                                    content="Delete"
                                    disabled={this.state.loading || this.state.selectedId === 0}
                                    action={EnumCommonAction.DELETE}
                                    onClick={this.onDeleteOrRestore}
                                />
                            }
                            {
                                !selectedActive &&
                                <Button
                                    icon="undo"
                                    positive={true}
                                    content="Restore"
                                    disabled={this.state.loading || this.state.selectedId === 0}
                                    action={EnumCommonAction.RESTORE}
                                    onClick={this.onDeleteOrRestore}
                                />
                            }
                            {
                                !selectedPosted &&
                                <Button
                                    icon="lock"
                                    primary={true}
                                    content="Post"
                                    disabled={this.state.loading || this.state.selectedId === 0}
                                    cutoffstatus={EnumCutoffStatus.POSTED}
                                    onClick={this.onPostUnpost}
                                />
                            }
                            {
                                selectedPosted &&
                                <Button
                                    icon="unlock"
                                    positive={true}
                                    content="Unpost"
                                    disabled={this.state.loading || this.state.selectedId === 0}
                                    cutoffstatus={EnumCutoffStatus.DRAFT}
                                    onClick={this.onPostUnpost}
                                />
                            }
                        </GridColumn>
                        <GridColumn textAlign="right">
                            <Form onSubmit={this.onFormSubmit}>
                                <FormInput
                                    disabled={this.state.loading}
                                    placeholder="Search"
                                    value={this.state.queryString}
                                    onChange={this.onQueryStringChanged}
                                >
                                    <input style={{ borderTopRightRadius: 0, borderBottomRightRadius: 0 }} />
                                    <Button icon="search" content="Search" primary={true} style={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }} />
                                    <Button icon="cog" type="button" onClick={this.showFilterDialog} />
                                </FormInput>
                            </Form>
                        </GridColumn>
                    </GridRow>
                    <GridRow style={{ paddingBottom: 40, paddingTop: 2 }}>
                        <GridColumn>
                            <Table celled={true} striped={true} selectable={true} sortable={true}>
                                <TableHeader>
                                    <TableRow>
                                        <TableHeaderCell width={3} data-columnname="startDate" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("startDate")}>Cutoff For</TableHeaderCell>
                                        <TableHeaderCell width={3} data-columnname="frequency" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("frequency")}>Frequency</TableHeaderCell>
                                        <TableHeaderCell width={3}>Date Range</TableHeaderCell>
                                        <TableHeaderCell width={3}>Status</TableHeaderCell>
                                        <TableHeaderCell width={4}>Remarks</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id} negative={!item.active}>
                                                    <TableCell>{this.createCutoffNoText(item)}</TableCell>
                                                    <TableCell>{item.frequency}</TableCell>
                                                    <TableCell>{item.startDate + " to " + item.endDate}</TableCell>
                                                    <TableCell>{item.status}</TableCell>
                                                    <TableCell>{item.remarks}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={5} textAlign="right">
                                            <Loader active={this.state.loading} />
                                            <Pagination
                                                disabled={this.state.loading}
                                                onPageChange={this.onPageChange}
                                                defaultActivePage={this.state.pageStat.currentPage}
                                                totalPages={this.state.pageStat.totalPageCount}
                                                ellipsisItem={{ content: <Icon name='ellipsis horizontal' />, icon: true }}
                                                firstItem={{ content: <Icon name='angle double left' />, icon: true }}
                                                lastItem={{ content: <Icon name='angle double right' />, icon: true }}
                                                prevItem={{ content: <Icon name='angle left' />, icon: true }}
                                                nextItem={{ content: <Icon name='angle right' />, icon: true }}
                                            />
                                        </TableHeaderCell>
                                    </TableRow>
                                </TableFooter>
                            </Table>
                        </GridColumn>
                    </GridRow>

                    <CutoffForm
                        visible={this.state.formVisible}
                        ref={this.formRef}
                        onCancelled={this.onFormCancelled}
                        onSaved={this.onFormSaved}
                    />

                    <DeleteRestoreDialog
                        onCancel={this.onCancelDeleteOrRestore}
                        onDelete={this.onDelete}
                        onRestore={this.onRestore}
                        open={this.state.deleteRestoreModalVisible}
                        size="small"
                        ref={this.deleteRestoreDialogRef}
                    />

                    <CutoffFilterDialog
                        onClose={this.onFilterDialogClose}
                        onReset={this.onFilterDialogReset}
                        onSaveAndSearch={this.onFilterDialogSaveAndSearch}
                        open={this.state.filterDialogVisible}
                        ref={this.filterDialogRef}
                        size="small"
                    />

                </Grid>
            </Container>
        );
    }

    private createCutoffNoText = (cutoff: Cutoff) => {
        let text = this.monthMap.get(cutoff.month)! + " " + cutoff.year;
        if (cutoff.frequency === EnumCutoffFrequency.SEMI_MONTHLY) {
            text += ", " + (cutoff.cutoffNumber === 1 ? "1st" : "2nd");
        }
        return text;
    }

    private onRowClick = (event: any) => {
        const id = event.currentTarget.getAttribute("data-id") as number;
        this.setState({ selectedId: +id });
    }

    private onQueryStringChanged = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        this.setState({ queryString: data.value });
    }

    private onFormSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        const pageStat = this.state.pageStat;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private onPageChange = (event: React.MouseEvent<HTMLElement, MouseEvent>, data: PaginationProps) => {
        const pageStat = this.state.pageStat;
        pageStat.currentPage = data.activePage as number;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private search = () => {
        const requestParam = new PagedSearchRequest();
        requestParam.includeInactive = this.state.pageStat.showInactive;
        requestParam.pageNumber = this.state.pageStat.currentPage;
        requestParam.pageSize = this.state.pageStat.pageSize;
        requestParam.queryString = this.state.queryString;
        requestParam.columnSorting = Util.columnSortingMapToObject(this.state.pageStat.columnSorting);
        requestParam.otherData = {
            year: this.state.filterByYear ? +this.state.year : 0,
            statuses: [EnumCutoffStatus.DRAFT, EnumCutoffStatus.POSTED].join(","),
            frequency: this.state.frequency
        };

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Cutoff>>("/cutoff/search", requestParam)
                    .then(result => {
                        if (result.status === EnumResponseStatus.SUCCESSFUL) {
                            const pageStat = this.state.pageStat;
                            pageStat.totalPageCount = result.totalPageCount;
                            this.setState({
                                contents: result.content,
                                loading: false,
                                pageStat
                            });
                        } else {
                            this.setState(
                                { loading: false },
                                () => {
                                    alert("Error: " + result.message);
                                }
                            );
                        }
                    });
            }
        );
    }

    private showForm = (id: number) => {
        fetchGet<Cutoff>("/cutoff/findById/" + id)
            .then(item => {
                this.setState(
                    { formVisible: true },
                    () => {
                        this.formRef.current!.setState({ content: item });
                    }
                );
            });
    }

    private onFormCancelled = () => {
        this.setState({ formVisible: false });
    }

    private onFormSaved = () => {
        this.formRef.current!.setState(
            { loading: true },
            () => {
                const content = this.formRef.current!.state.content;
                const url = "/cutoff/" + (content.id === 0 ? "create" : "edit");
                fetchPost<Cutoff, ResponseContainer<Cutoff>>(url, content)
                    .then(response => {
                        this.formRef.current!.setState(
                            { loading: false },
                            () => {
                                if (response.status === EnumResponseStatus.SUCCESSFUL) {
                                    const contents = this.state.contents;
                                    const index = contents.findIndex(item => item.id === response.content.id);
                                    if (index === -1) {
                                        contents.unshift(response.content);
                                    } else {
                                        contents[index] = response.content;
                                    }

                                    this.setState({
                                        formVisible: false,
                                        contents
                                    });
                                } else {
                                    this.formRef.current!.setState({
                                        loading: false,
                                        errorMap: Util.objectToMap(response.errorMap),
                                        errorMessage: response.message
                                    });
                                }
                            }
                        );
                    });
            }
        );
    }

    private onAdd = () => {
        this.showForm(0);
    }

    private onEdit = () => {
        if (this.state.selectedId === 0) {
            return;
        }

        this.showForm(this.state.selectedId);
    }

    private onDelete = () => {
        const id = this.deleteRestoreDialogRef.current!.state.id;
        this.deleteOrRestore(EnumCommonAction.DELETE, id as number);
    }

    private onDeleteOrRestore = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>, data: ButtonProps) => {
        const action = data.action as EnumCommonAction;
        this.setState(
            { deleteRestoreModalVisible: true },
            () => {
                this.deleteRestoreDialogRef.current!.setState({
                    header: "Confirm Action",
                    id: this.state.selectedId,
                    message: (action === EnumCommonAction.RESTORE ? "Restore " : "Delete ") + "selected cutoff?",
                    mode: action === EnumCommonAction.RESTORE ? EnumCommonAction.RESTORE : EnumCommonAction.DELETE
                });
            }
        );
    }

    private deleteOrRestore = (mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE, id: number) => {
        const url = "/cutoff/" + (mode === EnumCommonAction.RESTORE ? "restore" : "delete") + "/" + id;
        this.setState(
            { loading: true },
            () => {
                fetchGetNoReturn(url)
                    .then(() => {
                        let contents = this.state.contents;
                        if (mode === EnumCommonAction.RESTORE) {
                            const index = contents.findIndex(item => item.id === id);
                            contents[index].active = true;
                        } else {
                            if (this.state.pageStat.showInactive) {
                                contents.find(item => item.id === id)!.active = false;
                            } else {
                                contents = contents.filter(item => item.id !== this.state.selectedId);
                            }
                        }
                        this.setState({
                            contents: contents,
                            selectedId: 0,
                            deleteRestoreModalVisible: false,
                            loading: false
                        });
                    });
            }
        );
    }

    private showFilterDialog = () => {
        const pageStat = this.state.pageStat;
        this.setState(
            { filterDialogVisible: true },
            () => {
                this.filterDialogRef.current!.setState({
                    itemsPerPage: pageStat.pageSize,
                    showInactive: pageStat.showInactive,
                    filterByYear: this.state.filterByYear,
                    year: this.state.year
                });
            }
        );
    }

    private onColumnSort = (event: any) => {
        const columnname = event.currentTarget.getAttribute("data-columnname") as string;
        const pageStat = this.state.pageStat;
        const currentOrder = pageStat.columnSorting.get(columnname);
        const newOrder = currentOrder === undefined ? "ascending" : currentOrder === "ascending" ? "descending" : currentOrder === "descending" ? undefined : undefined;
        pageStat.columnSorting.set(columnname, newOrder);
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private onCancelDeleteOrRestore = () => {
        this.setState({ deleteRestoreModalVisible: false });
    }

    private onRestore = () => {
        const id = this.deleteRestoreDialogRef.current!.state.id;
        this.deleteOrRestore(EnumCommonAction.RESTORE, id as number);
    }

    private onFilterDialogClose = () => {
        this.setState({ filterDialogVisible: false });
    }

    private onFilterDialogReset = () => {
        this.filterDialogRef.current!.setState({
            itemsPerPage: 20,
            showInactive: false,
            filterByYear: false,
            year: new Date().getFullYear()
        });
    }

    private onFilterDialogSaveAndSearch = () => {
        const filterState = this.filterDialogRef.current!.state;
        const pageStat = this.state.pageStat;
        pageStat.pageSize = filterState.itemsPerPage;
        pageStat.showInactive = filterState.showInactive;
        pageStat.currentPage = 1;
        const year = filterState.year;
        const filterByYear = filterState.filterByYear;
        this.setState(
            { pageStat, filterDialogVisible: false, year, filterByYear },
            () => {
                this.search();
            }
        );
    }

    private onPostUnpost = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>, data: ButtonProps) => {
        const cutoffStatus = data.cutoffstatus as EnumCutoffStatus;
        const cutoffId = this.state.selectedId;
        const url = "/cutoff/" + (cutoffStatus === EnumCutoffStatus.POSTED ? "post" : "unpost") + "/" + cutoffId;
        fetchGet<ResponseContainer<void>>(url)
            .then(response => {
                if (response.status === EnumResponseStatus.SUCCESSFUL) {
                    const contents = this.state.contents;
                    const index = contents.findIndex(item => item.id === cutoffId);
                    contents[index].status = cutoffStatus;
                    this.setState({ contents });
                } else {
                    alert(response.message);
                }
            });
    }

}