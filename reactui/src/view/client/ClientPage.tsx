import * as React from 'react';
import {
    Button,
    ButtonProps,
    Container,
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
import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchGet, fetchGetNoReturn, fetchPost } from '../../common/Request';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Client from '../../model/Client';
import DeleteRestoreDialog from '../common/DeleteRestoreDialog';
import FilterDialog from '../common/FilterDialog';
import ClientForm from './ClientForm';

interface IState extends IPageState<Client> {
    deleteRestoreModalVisible: boolean,
    selectedClientNumber: string,
    filterDialogVisible: boolean
}

export default class ClientPage extends React.Component {

    public state: IState = {
        contents: new Array<Client>(),
        deleteRestoreModalVisible: false,
        formVisible: false,
        initialized: false,
        loading: false,
        name: "clientPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedId: 0,
        selectedClientNumber: "",
        filterDialogVisible: false
    }

    private formRef = React.createRef<ClientForm>();
    private deleteRestoreDialogRef = React.createRef<DeleteRestoreDialog>();
    private filterDialogRef = React.createRef<FilterDialog>();

    public componentDidMount() {
        if (!this.state.initialized) {
            const pageStat = this.state.pageStat;
            pageStat.columnSorting.set("name", "ascending");
            this.setState(
                { initialized: true },
                () => {
                    this.search();
                }
            );
        }
    }

    public render() {
        let selectedActive = true;
        if (!Util.isBlankOrNullString(this.state.selectedClientNumber)) {
            selectedActive = this.state.contents.filter(item => item.id === this.state.selectedClientNumber)[0].active;
        }
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Clients</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={2} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={8}>
                            <Button icon="add" primary={true} content="New" disabled={this.state.loading} onClick={this.onAdd} />
                            <Button icon="edit" primary={true} content="Edit" disabled={this.state.loading || Util.isBlankOrNullString(this.state.selectedClientNumber)} onClick={this.onEdit} />
                            {
                                selectedActive &&
                                <Button
                                    icon="trash"
                                    negative={true}
                                    content="Delete"
                                    disabled={this.state.loading || Util.isBlankOrNullString(this.state.selectedClientNumber)}
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
                                    disabled={this.state.loading ||  Util.isBlankOrNullString(this.state.selectedClientNumber)}
                                    action={EnumCommonAction.RESTORE}
                                    onClick={this.onDeleteOrRestore}
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
                                        <TableHeaderCell width={4} data-columnname="name" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("name")}>Name</TableHeaderCell>
                                        <TableHeaderCell width={2} data-columnname="id" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("id")}>Client No.</TableHeaderCell>
                                        <TableHeaderCell width={2} data-columnname="contactNumber" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("contactNumber")}>Contact No.</TableHeaderCell>
                                        <TableHeaderCell width={3} data-columnname="emailAddress" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("emailAddress")}>Email Address</TableHeaderCell>
                                        <TableHeaderCell width={5}>Address</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedClientNumber === item.id} negative={!item.active}>
                                                    <TableCell>{item.lastName + ", " + item.firstName + (Util.isBlankOrNullString(item.middleName) ? "" : " " + item.middleName)}</TableCell>
                                                    <TableCell>{item.id}</TableCell>
                                                    <TableCell>{item.contactNumber}</TableCell>
                                                    <TableCell>{item.emailAddress}</TableCell>
                                                    <TableCell>{item.address}</TableCell>
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

                    <ClientForm
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
        const selectedClientNumber = event.currentTarget.getAttribute("data-id") as string;
        this.setState({ selectedClientNumber });
    }

    private onQueryStringChanged = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        this.setState({ queryString: data.value });
    }

    private onFormSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        const pageStat = this.state.pageStat;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedClientNumber: "" },
            () => {
                this.search();
            }
        );
    }

    private onPageChange = (event: React.MouseEvent<HTMLElement, MouseEvent>, data: PaginationProps) => {
        const pageStat = this.state.pageStat;
        pageStat.currentPage = data.activePage as number;
        this.setState(
            { pageStat, selectedClientNumber: "" },
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
        if (columnSorting.has("name")) {
            const order = columnSorting.get("name");
            columnSorting.delete("name");
            columnSorting.set("lastName", order);
            columnSorting.set("firstName", order);
            columnSorting.set("middleName", order);
        }
        requestParam.columnSorting = Util.columnSortingMapToObject(columnSorting);

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Client>>("/client/search", requestParam)
                    .then(result => {
                        if (result.status === EnumResponseStatus.SUCCESSFUL) {
                            const pageStat = this.state.pageStat;
                            pageStat.totalPageCount = result.totalPageCount;
                            pageStat.columnSorting = Util.objectToColumnSortingMap(result.columnSorting);
                            if (pageStat.columnSorting.has("lastName")) {
                                const order = pageStat.columnSorting.get("lastName");
                                pageStat.columnSorting.delete("lastName");
                                pageStat.columnSorting.delete("firstName");
                                pageStat.columnSorting.delete("middleName");
                                pageStat.columnSorting.set("name", order);
                            }
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

    private showForm = (id: string) => {
        fetchGet<Client>("/client/findById/" + id)
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
                const url = "/client/" + (Util.isBlankOrNullString(content.id) ? "create" : "edit");
                fetchPost<Client, ResponseContainer<Client>>(url, content)
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
        this.showForm("");
    }

    private onEdit = () => {
        if (Util.isBlankOrNullString(this.state.selectedClientNumber)) {
            return;
        }

        this.showForm(this.state.selectedClientNumber);
    }

    private onDelete = () => {
        const id = this.deleteRestoreDialogRef.current!.state.id;
        this.deleteOrRestore(EnumCommonAction.DELETE, id as string);
    }

    private onDeleteOrRestore = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>, data: ButtonProps) => {
        const action = data.action as EnumCommonAction;
        this.setState(
            { deleteRestoreModalVisible: true },
            () => {
                this.deleteRestoreDialogRef.current!.setState({
                    header: "Confirm Action",
                    id: this.state.selectedClientNumber,
                    message: (action === EnumCommonAction.RESTORE ? "Restore " : "Delete ") + "selected client?",
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
        this.deleteOrRestore(EnumCommonAction.RESTORE, id as string);
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
            { pageStat, filterDialogVisible: false },
            () => {
                this.search();
            }
        );
    }

    private deleteOrRestore = (mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE, id: string) => {
        const url = "/client/" + (mode === EnumCommonAction.RESTORE ? "restore" : "delete") + "/" + id;
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
                                contents = contents.filter(item => item.id !== this.state.selectedClientNumber);
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

}