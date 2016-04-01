package entity;

public class ResultSet {
	private static final boolean SUCCESS = true;
	private static final boolean FAILURE = false;
	
	final static int CALENDAR_VIEW = 0;
	final static int TASK_VIEW = 1;
	final static int EXPANDED_VIEW = 2;
	final static int ASSOCIATE_VIEW = 3;
	final static int FLOATING_VIEW = 4;
	final static int SEARCH_VIEW = 5;
	
	final static int STATUS_GOOD =1;
	final static int STATUS_BAD =2;
	final static int STATUS_CONFLICT =3;
	final static int STATUS_PAST =3;
	
	private int _index;
	private int _view;
	
	
}
