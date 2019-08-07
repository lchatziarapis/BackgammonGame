
/**
 * The Heuristic algorithm class
 *
 */
public class Heuristic {

    public static int HeuristicValue(Tree_Node node){
            return ColumnAnalyze(node.getColumn_State());
    }
    public static int HeuristicValue(BoardState state){
        return ColumnAnalyze(state);
    }

    /**
     * Analyze the columns
     * and return an evaluation
     */
    private static int ColumnAnalyze(BoardState statei){
        int ownCaptured, opCaptured, ownLeft, opLeft, spread, value;
        int player = statei.playing;
        int ownColumns = 0; //When you have >2 chips on a column
        int ownSingle = 0;
        int fco = -1;    //First Chip Occurrence
        byte[][] columns = statei.GetColumns();
        int maxC = -1;
        value = 400;
        for (int i=0;i<columns.length;i++) {

            if(columns[i].length > 0 && columns[i][0] == player){
                if(fco ==-1) fco = i;
                maxC = i;
                if(columns[i].length > 1)
                    ownColumns++;
                else
                    ownSingle++;
            }
        }

        ownCaptured = statei.GetCaptured(player);
        opCaptured = statei.GetOpCaptured(player);
        ownLeft = 15- statei.GetWithdrawn(player);
        opLeft = 15- statei.GetOpWithdrawn(player);

        spread = maxC - fco;

        value += -(ownCaptured*15)-(ownSingle*25)+(ownColumns*60)+(opCaptured*10);
        //if i'm about to win
        if(ownLeft == 0){
            value = 10^4;
        }
        if(ownLeft < 15)
            value-= ownLeft*10;
        if(opLeft < 15)
            value+= opLeft*10;
        if(spread>10)
            value-=((spread-10)*5);

        return value;

    }

}

