import * as React from 'react';
import {
    Button,
    ButtonProps,
    Container,
    Dropdown,
    DropdownItemProps,
    DropdownOnSearchChangeData,
    DropdownProps,
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
import { EnumLoanStatus } from '../../common/EnumLoanStatus';
import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchGet, fetchGetNoReturn, fetchPost } from '../../common/Request';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Loan from '../../model/Loan';
import LoanType from '../../model/LoanType';
import DeleteRestoreDialog from '../common/DeleteRestoreDialog';
import FilterDialog from '../common/FilterDialog';
import LoanForm from './LoanForm';

interface IState extends IPageState<Loan> {
    loanTypes: DropdownItemProps[],
    selectedLoanTypeId: number,
    deleteRestoreModalVisible: boolean,
    filterDialogVisible: boolean
}

export default class LoanPage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<Loan>(),
        formVisible: false,
        initialized: false,
        loading: false,
        name: "loanPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedId: 0,
        loanTypes: new Array<DropdownItemProps>(),
        selectedLoanTypeId: 0,
        deleteRestoreModalVisible: false,
        filterDialogVisible: false
    }

    private formRef = React.createRef<LoanForm>();
    private loanTypeSearchTimeout: number = 0;
    private deleteRestoreDialogRef = React.createRef<DeleteRestoreDialog>();
    private filterDialogRef = React.createRef<FilterDialog>();

    public componentDidMount() {
        if (!this.state.initialized) {
            const pageStat = this.state.pageStat;
            pageStat.columnSorting.set("loanDate", "descending");
            const loanTypes = [{ key: 0, value: 0, text: "All Types" }];
            this.setState(
                { initialized: true, loanTypes, pageStat },
                () => {
                    this.search();
                }
            );
        }
    }

    public render() {
        let selectedActive = true;
        if (this.state.selectedId !== 0) {
            selectedActive = this.state.contents.filter(item => item.id === this.state.selectedId)[0].status === EnumLoanStatus.ACTIVE;
        }
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Loans</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={3} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={6}>
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
                        </GridColumn>
                        <GridColumn width={5}>
                            <Dropdown
                                fluid={true}
                                options={this.state.loanTypes}
                                value={this.state.selectedLoanTypeId}
                                selection={true}
                                disabled={this.state.loading}
                                onChange={this.onSelectedLoanTypeChanged}
                                search={true}
                                onSearchChange={this.onLoanTypeSearchChanged}
                                onClick={this.onLoanTypeSearchClick}
                            />
                        </GridColumn>
                        <GridColumn textAlign="right" width={5}>
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
                                        <TableHeaderCell width={2} data-columnname="loanDate" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("loanDate")}>Loan Date</TableHeaderCell>
                                        <TableHeaderCell width={5} data-columnname="client" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("client")}>Client</TableHeaderCell>
                                        <TableHeaderCell width={4} data-columnname="loanType" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("loanType")}>Loan Type</TableHeaderCell>
                                        <TableHeaderCell width={5}>Remarks</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id} negative={item.status === EnumLoanStatus.INACTIVE}>
                                                    <TableCell>{item.loanDate}</TableCell>
                                                    <TableCell>{item.client}</TableCell>
                                                    <TableCell>{item.loanType}</TableCell>
                                                    <TableCell>{item.remarks}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={4} textAlign="right">
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

                    <LoanForm
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

                    <FilterDialog
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
        pageStat.totalPageCount = 0;
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
        const columnSorting = this.state.pageStat.columnSorting;
        if (columnSorting.has("client")) {
            const order = columnSorting.get("client");
            columnSorting.delete("client");
            columnSorting.set("client.lastName", order);
            columnSorting.set("client.firstName", order);
            columnSorting.set("client.middleName", order);
        }
        requestParam.columnSorting = Util.columnSortingMapToObject(this.state.pageStat.columnSorting);
        requestParam.otherData = {
            loanTypeId: this.state.selectedLoanTypeId,
            loanStatuses: [EnumLoanStatus.ACTIVE].join(",")
        };

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Loan>>("/loan/search", requestParam)
                    .then(result => {
                        if (result.status === EnumResponseStatus.SUCCESSFUL) {
                            const pageStat = this.state.pageStat;
                            if (pageStat.columnSorting.has("client.lastName")) {
                                const order = pageStat.columnSorting.get("client.lastName");
                                pageStat.columnSorting.delete("client.lastName");
                                pageStat.columnSorting.delete("client.firstName");
                                pageStat.columnSorting.delete("client.middleName");
                                pageStat.columnSorting.set("client", order);
                            }
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
        fetchGet<Loan>("/loan/findById/" + id)
            .then(item => {
                this.setState(
                    { formVisible: true },
                    () => {
                        item.clientId = Util.isBlankOrNullString(item.clientId) ? "0" : item.clientId;
                        const loanTypes = [{
                            key: item.loanTypeId,
                            value: item.loanTypeId,
                            text: item.loanTypeId === 0 ? "Select Loan Type" : item.loanType
                        }];
                        const clients = [{
                            key: item.clientId,
                            value: item.clientId,
                            text: item.clientId === "0" ? "Select Client" : item.client
                        }];
                        this.formRef.current!.setState({
                            content: item,
                            errorMessage: "",
                            errorMap: new Map<string, string>(),
                            loanTypes,
                            clients
                        });
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
                const url = "/loan/" + (content.id === 0 ? "create" : "edit");
                fetchPost<Loan, ResponseContainer<Loan>>(url, content)
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

    private onSelectedLoanTypeChanged = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        this.setState(
            { selectedLoanTypeId: data.value as number },
            () => {
                const pageStat = this.state.pageStat;
                pageStat.currentPage = 1;
                this.setState(
                    { pageStat },
                    () => {
                        this.search();
                    }
                );
            }
        );
    }

    private getLoanTypes = (queryString: string) => {
        clearTimeout(this.loanTypeSearchTimeout);
        this.loanTypeSearchTimeout = window.setTimeout(() => {
            const requestParam = new PagedSearchRequest();
            requestParam.includeInactive = false;
            requestParam.pageNumber = 1;
            requestParam.pageSize = 20;
            requestParam.queryString = queryString;

            fetchPost<PagedSearchRequest, PagedSearchResponse<LoanType>>("/loantype/search", requestParam)
                .then(result => {
                    const loanTypes = new Array<DropdownItemProps>();
                    loanTypes.push({
                        key: 0,
                        value: 0,
                        text: "All Types"
                    });
                    result.content.forEach(item => {
                        loanTypes.push({
                            key: item.id,
                            value: item.id,
                            text: item.name
                        });
                    });
                    this.setState({ loanTypes });
                });
        }, 200);
    }

    private onLoanTypeSearchChanged = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownOnSearchChangeData) => {
        this.getLoanTypes(data.searchQuery);
    }

    private onLoanTypeSearchClick = (event: React.KeyboardEvent<HTMLElement>, data: DropdownProps) => {
        if (!event.currentTarget.firstChild) {
            return;
        }
        const node = (event.currentTarget.firstChild! as unknown) as { value: string };
        if (node !== undefined) {
            this.getLoanTypes(node.value);
        }
    }

    private onDeleteOrRestore = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>, data: ButtonProps) => {
        const action = data.action as EnumCommonAction;
        this.setState(
            { deleteRestoreModalVisible: true },
            () => {
                this.deleteRestoreDialogRef.current!.setState({
                    header: "Confirm Action",
                    id: this.state.selectedId,
                    message: (action === EnumCommonAction.RESTORE ? "Restore " : "Delete ") + "selected user profile?",
                    mode: action === EnumCommonAction.RESTORE ? EnumCommonAction.RESTORE : EnumCommonAction.DELETE
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
                    showInactive: pageStat.showInactive
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

    private deleteOrRestore = (mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE, id: number) => {
        const url = "/user/" + (mode === EnumCommonAction.RESTORE ? "restore" : "delete") + "/" + id;
        this.setState(
            { loading: true },
            () => {
                fetchGetNoReturn(url)
                    .then(() => {
                        let contents = this.state.contents;
                        const index = contents.findIndex(item => item.id === id);
                        if (mode === EnumCommonAction.RESTORE) {
                            contents[index].status = EnumLoanStatus.ACTIVE;
                        } else {
                            if (this.state.pageStat.showInactive) {
                                contents[index].status = EnumLoanStatus.INACTIVE;
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

    private onFilterDialogClose = () => {
        this.setState({ filterDialogVisible: false });
    }

    private onFilterDialogReset = () => {
        this.filterDialogRef.current!.setState({
            itemsPerPage: 20,
            showInactive: false
        });
    }

    private onFilterDialogSaveAndSearch = () => {
        const filterState = this.filterDialogRef.current!.state;
        const pageStat = this.state.pageStat;
        pageStat.pageSize = filterState.itemsPerPage;
        pageStat.showInactive = filterState.showInactive;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, filterDialogVisible: false, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

}