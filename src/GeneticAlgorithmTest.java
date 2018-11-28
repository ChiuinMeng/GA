
//遗传算法的类是一个抽象类，因此我们需要针对特定的事例编写实现类，假设我们计算 Y=100-log(X)在[6,106]上的最值。
public class GeneticAlgorithmTest extends GeneticAlgorithm{
    //我们假设基因的长度为24（基因的长度由要求结果的有效长度确定），因此对应的二进制最大值为 1<< 24，我们做如下设置
    public static final int NUM = 1 << 24;

    public GeneticAlgorithmTest() {
        super(24);
    }

    //对X值的抽象方法进行实现
    @Override
    public double changeX(Chromosome chro) {
        // TODO Auto-generated method stub
        return ((1.0 * chro.getNum() / NUM) * 100) + 6;
    }

    //对Y的抽象方法进行实现
    @Override
    public double caculateY(double x) {
        // TODO Auto-generated method stub
        return 100 - Math.log(x);
    }

    public static void main(String[] args) {
        GeneticAlgorithmTest test = new GeneticAlgorithmTest();
        test.caculte();
    }
}
