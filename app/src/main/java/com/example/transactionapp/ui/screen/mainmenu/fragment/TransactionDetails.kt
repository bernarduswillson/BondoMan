
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentTransactionDetailsBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal

class TransactionDetails : Fragment() {

    private val db : TransactionViewModel by activityViewModels()

    companion object {
        const val ARG_TRANSACTION_ID = "transaction_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTransactionDetailsBinding.inflate(layoutInflater)

        val transactionId = requireArguments().getInt(ARG_TRANSACTION_ID)

        db.getTransactionById(transactionId)
        db.transactionById.observe(viewLifecycleOwner) {
            binding.titleInput.setText(it.title)

            binding.dateInput.text = changeDateTypeToStandardDateLocal(it.createdAt)

            val categories = arrayOf("Income", "Expense", "Savings")
            val arrayAdp = ArrayAdapter(requireActivity(), R.layout.selected_dropdown_item, categories)
            arrayAdp.setDropDownViewResource(R.layout.dropdown_item)
            binding.categoryInput.adapter = arrayAdp
            binding.categoryInput?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d("TransactionForm", "onItemSelected: ${categories[position]}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("TransactionForm", "onNothingSelected: ")
                }
            }
            binding.categoryInput.setSelection(categories.indexOf(it.category))

            binding.amountInput.setText(it.nominal.toString())

            binding.locationInput.text = it.location
        }

        binding.saveTransactionButton.setOnClickListener {
            if (binding.titleInput.text.toString() == ""){
                return@setOnClickListener
            }
            if (binding.amountInput.text.toString() == ""){
                return@setOnClickListener
            }

            if (binding.titleInput.text.toString() != "" && binding.amountInput.text.toString() != ""){
                db.updateTransaction(
                    Transaction(
                        id = transactionId,
                        title = binding.titleInput.text.toString(),
                        category = binding.categoryInput.selectedItem.toString(),
                        nominal = binding.amountInput.text.toString().toLong(),
                        createdAt = db.transactionById.value?.createdAt!!,
                        location = binding.locationInput.text.toString(),
                        lat = db.transactionById.value?.lat!!,
                        long = db.transactionById.value?.long!!
                    )
                )
                db.changeAddStatus(true)
                Toast.makeText(requireContext(), "Transaction Updated", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        binding.deleteTransactionButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete this transaction?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    db.deleteTransaction(db.transactionById.value!!)
                    db.changeAddStatus(true)
                    Toast.makeText(requireContext(), "Transaction Deleted", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        return binding.root
    }
}