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
import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchGet, fetchGetNoReturn, fetchPost } from '../../common/Request';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Group from '../../model/Group';
import GroupType from '../../model/GroupType';
import DeleteRestoreDialog from '../common/DeleteRestoreDialog';
import FilterDialog from '../common/FilterDialog';
import GroupForm from './GroupForm';

interface IState extends IPageState<Group> {
    deleteRestoreModalVisible: boolean,
    filterDialogVisible: boolean,
    groupTypes: DropdownItemProps[],
    selectedGroupTypeId: number,
    groupTypeSearchString: string
}

export default class GroupPage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<Group>(),
        deleteRestoreModalVisible: false,
        formVisible: false,
        groupTypes: new Array<DropdownItemProps>(),
        initialized: false,
        loading: false,
        name: "groupPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedGroupTypeId: 0,
        selectedId: 0,
        groupTypeSearchString: "",
        filterDialogVisible: false
    }

    private formRef = React.createRef<GroupForm>();
    private groupTypeSearchTimeout: number = 0;
    private deleteRestoreDialogRef = React.createRef<DeleteRestoreDialog>();
    private filterDialogRef = React.createRef<FilterDialog>();

    public componentDidMount() {
        if (!this.state.initialized) {
            const groupTypes = [{ key: 0, value: 0, text: "All Types" }];
            const pageStat = this.state.pageStat;
            pageStat.columnSorting.set("name", "ascending");
            this.setState(
                { initialized: true, groupTypes, pageStat },
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
                            <Header as="h2">Groups</Header>
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
                                options={this.state.groupTypes}
                                value={this.state.selectedGroupTypeId}
                                selection={true}
                                disabled={this.state.loading}
                                onChange={this.onSelectedGroupTypeChanged}
                                search={true}
                                onSearchChange={this.onGroupTypeSearchChanged}
                                onClick={this.onGroupTypeSearchClick}
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
                                        <TableHeaderCell width={3} data-columnname="name" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("name")}>Name</TableHeaderCell>
                                        <TableHeaderCell width={5} data-columnname="type.name" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("type.name")}>Type</TableHeaderCell>
                                        <TableHeaderCell width={8} data-columnname="description" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("description")}>Description</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id} negative={!item.active}>
                                                    <TableCell>{item.name}</TableCell>
                                                    <TableCell>{item.groupType}</TableCell>
                                                    <TableCell>{item.description}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={3} textAlign="right">
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

                    <GroupForm
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
        requestParam.columnSorting = Util.columnSortingMapToObject(this.state.pageStat.columnSorting);
        requestParam.otherData = { groupTypeId: this.state.selectedGroupTypeId };

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Group>>("/group/search", requestParam)
                    .then(result => {
                        if (result.status === EnumResponseStatus.SUCCESSFUL) {
                            const pageStat = this.state.pageStat;
                            pageStat.totalPageCount = result.totalPageCount;
                            pageStat.columnSorting = Util.objectToColumnSortingMap(result.columnSorting);
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
        fetchGet<Group>("/group/findById/" + id)
        .then((item) => {
            this.setState(
                { formVisible: true },
                () => {
                    this.formRef.current!.setState({
                        content: item,
                        errorMessage: "",
                        errorMap: new Map<string, string>()
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

                const url = "/group/" + (content.id === 0 ? "create" : "edit");
                fetchPost<Group, ResponseContainer<Group>>(url, content)
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

    private onSelectedGroupTypeChanged = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        this.setState(
            { selectedGroupTypeId: data.value as number },
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

    private getGroupTypes = (queryString: string) => {
        clearTimeout(this.groupTypeSearchTimeout);
        this.groupTypeSearchTimeout = window.setTimeout(() => {
            const requestParam = new PagedSearchRequest();
            requestParam.includeInactive = false;
            requestParam.pageNumber = 1;
            requestParam.pageSize = 20;
            requestParam.queryString = queryString;

            fetchPost<PagedSearchRequest, PagedSearchResponse<GroupType>>("/grouptype/search", requestParam)
                .then(result => {
                    const groupTypes = new Array<DropdownItemProps>();
                    groupTypes.push({
                        key: 0,
                        value: 0,
                        text: "All Types"
                    });
                    result.content.forEach(item => {
                        groupTypes.push({
                            key: item.id,
                            value: item.id,
                            text: item.name
                        });
                    });
                    this.setState({ groupTypes });
                });
        }, 200);
    }

    private onGroupTypeSearchChanged = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownOnSearchChangeData) => {
        this.getGroupTypes(data.searchQuery);
    }

    private onGroupTypeSearchClick = (event: React.KeyboardEvent<HTMLElement>, data: DropdownProps) => {
        if (!event.currentTarget.firstChild) {
            return;
        }
        const node = (event.currentTarget.firstChild! as unknown) as { value: string };
        if (node !== undefined) {
            this.getGroupTypes(node.value);
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

    private deleteOrRestore = (mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE, id: number) => {
        const url = "/group/" + (mode === EnumCommonAction.RESTORE ? "restore" : "delete") + "/" + id;
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