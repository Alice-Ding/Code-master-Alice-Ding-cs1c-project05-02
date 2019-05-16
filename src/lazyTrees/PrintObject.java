package lazyTrees;

public class PrintObject<E> implements Traverser<E> {

    public void visit(E x){

        System.out.print(x + " ");
    }
}
