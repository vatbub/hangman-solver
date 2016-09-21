package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultList extends ArrayList<Result> {

	private static final long serialVersionUID = 1114264855287178435L;

	public ResultList() {
		super();
	}

	public ResultList(int arg0) {
		super(arg0);
	}

	public ResultList(Collection<? extends Result> arg0) {
		super(arg0);
	}
	
	public List<ResultType> getResultTypeList(){
		List<ResultType> res =new ArrayList<ResultType>();
		for (Result result:this){
			res.add(result.resultType);
		}
		
		return res;
	}
	
	public List<String> getBestWords(){
		List<String> res =new ArrayList<String>();
		for (Result result:this){
			res.add(result.bestWord);
		}
		
		return res;
	}
	
	public Result getFirstResultWithBestChar(char chr){
		for (Result res:this){
			if (res.bestChar==chr){
				return res;
			}
		}
		
		// We only arrive here if no match has been found
		return null;
	}

}
