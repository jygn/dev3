/**
 * @author Jessy Grondin (20119453)
 * @author Prénom Nom (Matricule)
 *
 */
public class BloomFilter {

    private int numHashes;
    private BitSet bitSet;
    private int nb_elems;

    /**
     * Crée un filtre de Bloom basé sur la taille de l'ensemble de bits et du
     * nombre de fonctions de hachage.
     *
     * @param numBits taille de l'ensemble de bits
     * @param numHashes nombre de fonctions de hachage
     */
    public BloomFilter(int numBits, int numHashes) {
        this.bitSet = new BitSet(numBits);
        this.numHashes = numHashes;
        this.nb_elems = 0;   // nb d'éléments ajoutés
    }

    /**
     * Crée un filtre de Bloom basé sur le nombre d'éléments attendus et de la
     * probabilité de faux positifs désirée.
     *
     * @param numElems nombre d'éléments à insérer
     * @param falsePosProb probabilité de faux positifs
     *
     */
    public BloomFilter(int numElems, double falsePosProb) {
        int m = (int)Math.ceil(((-numElems*Math.log(falsePosProb))/Math.pow(Math.log(2), 2)));   // m = -(nln(p))/(ln2)²
        int k = (int)Math.ceil((-Math.log(falsePosProb)/Math.log(2)));  // k = -log_2(p)
        this.bitSet = new BitSet(m);
        this.numHashes = k;
        this.nb_elems = 0;
    }

    /**
     * Ajoute un élément au filtre de Bloom.
     *
     * @param key l'élément à insérer
     */
    public void add(byte[] key) {

        if (key.length == 0)    // null
            return;

        int bit_index;

        for (int i = 1; i <= numHashes; i++) { // active les filtres
            bit_index = hash(key, i); // hash 0, 1, ... n
            bitSet.set(bit_index);
        }

        nb_elems++;
    }

    /**
     * Cherche pour l'élément dans le filtre de Bloom.
     *
     * @param key l'élément à trouver
     * @return si l'élément est possiblement dans le filtre
     */
    public boolean contains(byte[] key) {

        if (numHashes < 1)
            return false;

        int bit_index;

        for (int i = 0; i < numHashes; i++) {
            bit_index = hash(key, i); // hash 0, 1, ... n
            if (!bitSet.get(bit_index))
                return false;
        }

        return true;
    }

    /**
     * Remet à zéro le filtre de Bloom.
     */
    public void reset() {
        bitSet.clearAll();
        nb_elems = 0;
    }

    /**
     * Retourne le nombre de bits du filtre de Bloom.
     *
     * @return nombre de bits
     */
    public int size() { // TODO bien la taille du tableau de bits??
        return bitSet.getBset_len();
    }

    /**
     * Retourne le nombre d'éléments insérés dans le filtre de Bloom.
     *
     * @return nombre d'éléments insérés
     */
    public int count() { return nb_elems; }

    /**
     * Retourne la probabilité actuelle de faux positifs.
     *
     * @return probabilité de faux positifs
     */
    public double fpp() {
        double p = Math.exp(-numHashes * (double)count() / size());   // p = e^(-kn/m)
        return Math.pow((1 - p), numHashes);    // (1 - p)^k
    }


    /**
     * Hash function qui utilise l'encodage ASCII
     *
     * Source : M. A. Weiss, Data Structures and Algorithm Analysis in Java (Third
     *          Edition), 2012
     *
     */
    public int hash (byte[] key, int fn) { // TODO: à travailler..

        int hash = 17;

//        int f = fn*7;
//        int i = 2;

//        while (true) {
//            if (f % i != 0) {  // nombre premier?
//                f = i;
//                break;
//            }
//            i++;
//        }

        for (byte b : key) {
            hash = 37 * hash + b;
        }

        return hash % size(); // ne depasse pas taille du tableau de bits
    }

    public static void main (String args[]) {

        BloomFilter bf = new BloomFilter(4, 0.0044);
//        BloomFilter bf = new BloomFilter(2000, 3);

        byte[] key1 = {};
        byte[] key2 = {'r','d','e','d'};

        bf.add(key1);
        bf.add(key2);

        System.out.println(bf.contains(key1));
        System.out.println(bf.contains(key2));

//        bf.reset();
        System.out.println("k:" + bf.numHashes);
        System.out.println("n:" + bf.count());
        System.out.println("m:" + bf.size());
        System.out.println("Prob faux positif = "+ bf.fpp());


        bf.reset();
    }
}
