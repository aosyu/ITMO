package queue;

/*
Model:
    [a1, a2, ... an]
    n -- размер очереди

Inv:
    n >= 0
    forall i = 1..n: a[i] != null

Immutable: n == n' && forall i = 1..n: a[i] == a'[i]
 */

public interface Queue {
    /*
        Pred: e != null
        Post: a'[1] = e && n = n' + 1 && forall i = 1..n': a'[i + 1] = a[i]
     */
    void push(Object e);

    /*
        Pred: n > 0
        Post: R == a[n] && Immutable
     */
    Object peek();

    /*
        Pred: n > 0
        Post: R == a'[n'] && n = n' - 1 && forall i = 1..n: a'[i] = a[i]
     */
    Object remove();

    /*
        Pred: e != null
        Post: n = n' + 1 && a[n] == e && forall i = 1..n': a[i] == a'[i]
     */
    void enqueue(Object e);

     /*
        Pred: n > 0
        Post: R == a[1] && Immutable
     */
    Object element();

    /*
        Pred: true
        Post: Immutable && R == n
     */
    int size();

    /*
        Pred: n > 0
        Post: R == a[1] && forall i = 2..n: a'[i] = a[i - 1] && n = n' - 1
     */
    Object dequeue();

     /*
        Pred: true
        Post: R == (n == 0) && Immutable
     */
    boolean isEmpty();

    /*
        Pred: true
        Post: n == 0
     */
    void clear();

    /*
        Pred: true
        Post: R == exist k: (1 <= k <= n && a[k] == e) && Immutable
     */
    boolean contains(Object e);

    /*
        Pred: true
        Post: M = {j : (1 <= j <= n' && a'[j] == e)} ∪ {n' + 1} 
        && R == k ∈ M: forall el in M: el >= k
        && a[1..k-1] == a'[1..k-1] && a[k+1..n] == a'[k+1..n]
        && n = max(R, n') - 1  	  
     */
    boolean removeFirstOccurrence(Object e);
}
