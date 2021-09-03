(defn areVectors [vs]
  (every? (fn [v] (and (vector? v) (every? number? v))) vs))

(defn equalSizes [vs]
  (apply == (mapv count vs)))

(defn checkVectors [vs]
  (and (areVectors vs) (equalSizes vs)))

; :NOTE: Столбцы
(defn checkMs [ms]
  (and (every? (fn [m] (and (vector? m) (checkVectors m))) ms)
       (equalSizes ms)))

(defn vectorOp [f] (fn [& vs]
                     {:pre [(checkVectors vs)]}
                     (apply mapv f vs)))

(defn matrixOp [f] (fn [& ms]
                     {:pre [(checkMs ms)]}
                     (apply mapv f ms)))

(def v+ (vectorOp +))
(def v- (vectorOp -))
(def v* (vectorOp *))
(def vd (vectorOp /))
(defn scalar [& vs] (apply + (apply v* vs)))


(defn crdSubMul [v1 v2 crd1 crd2]
  (- (* (nth v1 crd1) (nth v2 crd2))
     (* (nth v2 crd1) (nth v1 crd2))))

(defn vect [& vs]
  {:pre [(checkVectors vs)]}
  (reduce (fn [v1 v2]
            (vector (crdSubMul v1 v2 1 2)
                    (crdSubMul v1 v2 2 0)
                    (crdSubMul v1 v2 0 1)))
          vs))

(defn v*s [v & x]
  {:pre [(and (every? number? x) (areVectors (list v)))]}
  (mapv (partial * (apply * x)) v))


(def m+ (matrixOp v+))
(def m- (matrixOp v-))
(def m* (matrixOp v*))
(def md (matrixOp vd))

(defn m*s [m & s]
  {:pre [(every? number? s)]}
  (let [smul (reduce * s)]
    (mapv (fn [x] (v*s x smul)) m)))

(defn m*v [m & v]
  {:pre [(areVectors v)]}
  (let [vmul (if (empty? (rest v)) (first v) (reduce scalar v))]
    (mapv (fn [x] (scalar x vmul)) m)))

(defn transpose [m] (apply mapv vector m))

(defn m*m [& ms]
  {:pre [(every? (fn [x] (and (vector? x)
                              (checkVectors x))) ms)]}
  (reduce (fn [m1 m2] (let [trans_m2 (transpose m2)] (mapv (fn [v] (m*v trans_m2 v)) m1))) ms))



(defn isSimplex [s]
  (or (and (vector? s) (every? number? s))
      (letfn [(check [s1 s2] (if (== (count s1) (inc (count s2))) s2 (vector)))]
        (and (> (count (reduce check (conj s 1) s)) 0)
             (every? isSimplex s)))))

(defn checkSimplexes [ss]
  (and (every? isSimplex ss) (equalSizes ss)))

(defn x [f]
  (letfn [(get-result [& ss]
            {:pre [(or (every? number? ss) (and (not (every? empty? ss)) (checkSimplexes ss)))]}
            (if (every? number? ss)
              (apply f ss)
              (apply mapv get-result ss))
            )]
    get-result))

(def x+ (x +))
(def x- (x -))
(def x* (x *))
(def xd (x /))
