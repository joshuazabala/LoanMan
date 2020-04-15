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
import GroupType from '../../model/GroupType';
import DeleteRestoreDialog from '../common/DeleteRestoreDialog';
import FilterDialog from '../common/FilterDialog';
import GroupTypeForm from './GroupTypeForm';

interface IState extends IPageState<GroupType> {
    deleteRestoreModalVisible: boolean,
    filterDialogVisible: boolean
}

export default class GroupTypePage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<GroupType>(),
        name: "groupTypePage",
        selectedId: 0,
        loading: false,
        pageStat: new PageStat(),
        queryString: "",
        formVisible: false,
        deleteRestoreModalVisible: false,
        initialized: false,
        filterDialogVisible: false
    }

    private formRef = React.createRef<GroupTypeForm>();
    private deleteRestoreDialogRef = React.createRef<DeleteRestoreDialog>();
    private filterDialogRef = React.createRef<FilterDialog>();

    public componentDidMount() {
        if (!this.state.initialized) {
            const pageStat = this.state.pageStat;
            pageStat.columnSorting.set("name", "ascending");
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
        if (this.state.selectedId !== 0) {
            selectedActive = this.state.contents.filter(item => item.id === this.state.selectedId)[0].active;
        }
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Group Types</Header>
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
                                        <TableHeaderCell width={3} data-columnname="name" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("name")}>Name</TableHeaderCell>
                                        <TableHeaderCell width={13} data-columnname="description" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("description")}>Description</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id} negative={!item.active}>
                                                    <TableCell>{item.name}</TableCell>
                                                    <TableCell>{item.description}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={2} textAlign="right">
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

                    <GroupTypeForm
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
        const groupTypeId = event.currentTarget.getAttribute("data-id") as number;
        this.setState({ selectedId: +groupTypeId });
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

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<GroupType>>("/grouptype/search", requestParam)
                    .then(result => {
                        const pageStat = this.state.pageStat;
                        pageStat.totalPageCount = result.totalPageCount;
                        pageStat.columnSorting = Util.objectToColumnSortingMap(result.columnSorting);
                        this.setState({
                            contents: result.content,
                            loading: false,
                            pageStat
                        });
                    });
            }
        );
    }

    private showForm = (id: number) => {
        fetchGet<GroupType>("/grouptype/findById/" + id)
            .then(item => {
                this.setState(
                    { formVisible: true },
                    () => {
                        this.formRef.current!.setState({ content: item, errorMap: new Map<string, string>() });
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

                const url = "/grouptype/" + (content.id === 0 ? "create" : "edit");
                fetchPost<GroupType, ResponseContainer<GroupType>>(url, content)
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
                    message: (action === EnumCommonAction.RESTORE ? "Restore " : "Delete ") + "selected group type?",
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

    private deleteOrRestore = (mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE, id: number) => {
        const url = "/grouptype/" + (mode === EnumCommonAction.RESTORE ? "restore" : "delete") + "/" + id;
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

}