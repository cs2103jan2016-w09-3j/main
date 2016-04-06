package entity;

public class ResultSet {
    private static final boolean SUCCESS = true;
    private static final boolean FAILURE = false;

    public final static int CALENDAR_VIEW = 0;
    public final static int TASK_VIEW = 1;
    public final static int EXPANDED_VIEW = 2;
    public final static int ASSOCIATE_VIEW = 3;
    public final static int FLOATING_VIEW = 4;
    public final static int SEARCH_VIEW = 5;

    public final static int STATUS_GOOD = 1;
    public final static int STATUS_BAD = 2;
    public final static int STATUS_CONFLICT = 3;
    public final static int STATUS_PAST = 4;
    public final static int STATUS_CONFLICT_AND_PAST = 5;
    public final static int STATUS_INVALID_NAME = 6;
    public final static int STATUS_NOFILE = 7;

    private int _index;
    private int _view;
    private int _status;
    private boolean _isSuccess;
    private int _searchCount;

    public ResultSet () {
        _index = -1;
    }
    
    public int getIndex() {
        return _index;
    }

    public void setIndex(int index) {
        _index = index;
    }

    public int getView() {
        return _view;
    }

    public void setView(int view) {
        _view = view;
    }

    public int getStatus() {
        return _status;
    }

    public void setStatus(int status) {
        // Statuses are added on rather than overwritten for warning messages
        if (status == STATUS_CONFLICT && _status == STATUS_PAST) {
            _status = STATUS_CONFLICT_AND_PAST;
        } else if (status == STATUS_PAST && _status == STATUS_CONFLICT) {
            _status = STATUS_CONFLICT_AND_PAST;
        } else if (status == STATUS_GOOD && (_status == STATUS_CONFLICT || _status == STATUS_PAST
                || _status == STATUS_CONFLICT_AND_PAST)) {
            // Do nothing - Keeps the warning messages not overwritten by good outcomes
        } else {
            _status = status;
        }
    }

    public boolean isSuccess() {
        return _isSuccess;
    }

    public void setSuccess() {
        _isSuccess = SUCCESS;
    }
    
    public void setFail() {
        _isSuccess = FAILURE;
    }
    
    public void setSearchCount (int searchCount) {
        _searchCount = searchCount;
    }
    
    public int getSearchCount() {
        return _searchCount;
    }
}
