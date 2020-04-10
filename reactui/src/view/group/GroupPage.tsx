import * as React from 'react';
import {
    Button,
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
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
    Pagination,
    PaginationProps,
    Table,
    TableBody,
    TableCell,
    TableFooter,
    TableHeader,
    TableHeaderCell,
    TableRow,
    Container,
} from 'semantic-ui-react';

import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchPost } from '../../common/Request';
import RequestContainer from '../../common/RequestContainer';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Group from '../../model/Group';
import GroupType from '../../model/GroupType';
import GroupForm from './GroupForm';

interface IState extends IPageState<Group> {
    deleteModalVisible: boolean,

    groupTypes: DropdownItemProps[],
    selectedGroupTypeId: number,
    groupTypeSearchString: string
}

export default class GroupPage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<Group>(),
        deleteModalVisible: false,
        formVisible: false,
        groupTypes: new Array<DropdownItemProps>(),
        initialized: false,
        loading: false,
        name: "groupPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedGroupTypeId: 0,
        selectedId: 0,
        groupTypeSearchString: ""
    }

    private formRef = React.createRef<GroupForm>();
    private groupTypeSearchTimeout: number = 0;

    public componentDidMount() {
        if (!this.state.initialized) {
            const groupTypes = [{ key: 0, value: 0, text: "All Types" }];
            this.setState(
                { initialized: true, groupTypes },
                () => {
                    this.search();
                }
            );
        }
    }

    public render() {
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Groups</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={3} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={5}>
                            <Button icon="add" primary={true} content="New" disabled={this.state.loading} onClick={this.onAdd} />
                            {this.state.selectedId !== 0 && <Button icon="edit" content="Edit" disabled={this.state.loading} onClick={this.onEdit} />}
                            {this.state.selectedId !== 0 && <Button icon="trash" content="Delete" disabled={this.state.loading} onClick={this.onDelete} />}
                        </GridColumn>
                        <GridColumn width={6}>
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
                                    action={{ icon: "search", content: "Search" }}
                                    value={this.state.queryString}
                                    onChange={this.onQueryStringChanged}
                                />
                            </Form>
                        </GridColumn>
                    </GridRow>
                    <GridRow style={{ paddingBottom: 40, paddingTop: 2 }}>
                        <GridColumn>
                            <Table celled={true} striped={true} selectable={true}>
                                <TableHeader>
                                    <TableRow>
                                        <TableHeaderCell width={2}>Code</TableHeaderCell>
                                        <TableHeaderCell width={5}>Type</TableHeaderCell>
                                        <TableHeaderCell width={9}>Description</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id}>
                                                    <TableCell>{item.code}</TableCell>
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

                    <Modal open={this.state.deleteModalVisible} size="small">
                        <ModalHeader>Confirm Action</ModalHeader>
                        <ModalContent>Delete selected group?</ModalContent>
                        <ModalActions>
                            <Button
                                negative={true}
                                content="Delete"
                                onClick={this.delete}
                                disabled={this.state.loading}
                            />
                            <Button
                                content="Cancel"
                                onClick={this.onCancelDelete}
                                disabled={this.state.loading}
                            />
                        </ModalActions>
                        <Loader active={this.state.loading} />
                    </Modal>

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
            { pageStat },
            () => {
                this.search();
            }
        );
    }

    private search = () => {
        const requestParam = new PagedSearchRequest();
        requestParam.includeInactive = false;
        requestParam.pageNumber = this.state.pageStat.currentPage;
        requestParam.pageSize = this.state.pageStat.pageSize;
        requestParam.queryString = this.state.queryString;
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
        const requestParam = { id };

        fetchPost<{ id: number }, Group>("/group/findById", requestParam)
            .then(item => {
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
                const requestParam = new RequestContainer<Group>();
                requestParam.content = content;

                fetchPost<RequestContainer<Group>, ResponseContainer<Group>>("/group/save", requestParam)
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
        this.setState({ deleteModalVisible: true });
    }

    private onCancelDelete = () => {
        this.setState({ deleteModalVisible: false });
    }

    private delete = () => {
        const requestParam = new RequestContainer<number>();
        requestParam.content = this.state.selectedId;

        this.setState(
            { loading: true },
            () => {
                fetchPost<RequestContainer<number>, ResponseContainer<boolean>>("/group/delete", requestParam)
                    .then(response => {
                        if (response.status === EnumResponseStatus.SUCCESSFUL) {
                            let contents = this.state.contents;
                            contents = contents.filter(item => item.id !== this.state.selectedId);
                            this.setState({
                                contents: contents,
                                selectedId: 0,
                                deleteModalVisible: false,
                                loading: false
                            });
                        } else {
                            alert("Error: " + response.message);
                        }
                    });
            }
        );
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
                            text: item.code + " - " + item.description
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

}